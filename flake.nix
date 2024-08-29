{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-24.05";
    build-gradle-application.url = "github:raphiz/buildGradleApplication";
    pre-commit-hooks.url = "github:cachix/pre-commit-hooks.nix";
    nix-npm-buildpackage.url = "github:serokell/nix-npm-buildpackage";
  };

  outputs = inputs @ {
    self,
    nixpkgs,
    flake-parts,
    build-gradle-application,
    pre-commit-hooks,
    nix-npm-buildpackage,
    ...
  }: let
    version = "0.1.0";
  in
    flake-parts.lib.mkFlake {inherit inputs;} {
      systems = nixpkgs.lib.systems.flakeExposed;
      flake = {
        overlays = {
          default = final: prev: {
            kotlin-ssr-showcase = self.packages.${prev.system}.default;
          };
          dev = final: prev: {
            jdk = prev.jdk21_headless;
            jre_headless = prev.jdk21_headless;
            nodejs = prev.nodejs_22;
            ktlint = prev.ktlint;
            detekt = prev.detekt;
            gradle = prev.callPackage (prev.gradleGen {
              defaultJava = final.jdk;
              version = "8.8";
              nativeVersion = "0.22-milestone-26";
              hash = "sha256-pLQVhgH4Y2ze6rCb12r7ZAAwu1sUSq/iYaXorwJ9xhI=";
            }) {};
          };
        };
        nixosModules.kotlin-ssr-showcase = import ./module.nix;
      };
      perSystem = {
        config,
        system,
        ...
      }: let
        pkgs = import nixpkgs {
          inherit system;
          overlays = [
            nix-npm-buildpackage.overlays.default
            build-gradle-application.overlays.default
            self.overlays.dev
            self.overlays.default
          ];
        };
      in {
        packages = {
          default =
            pkgs.callPackage ./package.nix
            {
              inherit version;
            };
        };
        checks = {
          integrationTest = pkgs.nixosTest (import ./integration-test.nix {
            kotlin-ssr-showcase-module = self.nixosModules.kotlin-ssr-showcase;
          });
          pre-commit-check = pre-commit-hooks.lib.${system}.run {
            src = ./.;
            hooks = {
              alejandra.enable = true;
              convco.enable = true;
              detekt = {
                enable = true;
                name = "detekt";
                entry = let
                  script = pkgs.writeShellScriptBin "detekt-wrapper" ''set -euo pipefail; IFS=','; ${pkgs.detekt}/bin/detekt --build-upon-default-config -c detekt-config.yml --auto-correct -i "$*"; unset IFS;'';
                in "${script}/bin/detekt-wrapper";
                files = "\\.(kt|kts)$";
                language = "system";
              };
              ktlint = {
                enable = true;
                name = "ktlint";
                entry = "${pkgs.ktlint}/bin/ktlint --format";
                files = "\\.(kt|kts)$";
                language = "system";
              };
            };
          };
        };
        devShells.default = let
          # Scripts use process compose, see https://f1bonacc1.github.io/process-compose/launcher/
          scripts = {
            build = {
              processes = {
                frontend.command = ''npm ci && npx vite build'';
                backend = {
                  command = ''gradle :clean :check :installDist'';
                  depends_on.frontend.condition = "process_completed";
                };
              };
            };

            build-continuously = {
              processes = {
                "init frontend".command = ''npm ci && npx vite build'';
                frontend = {
                  command = ''npx vite build -w'';
                  depends_on."init frontend".condition = "process_completed";
                };
                backend = {
                  command = ''gradle --continuous :check :installDist'';
                  depends_on."init frontend".condition = "process_completed";
                };
              };
            };

            dev = {
              processes = {
                "init frontend".command = ''npm ci && npx vite build'';
                frontend = {
                  command = ''npx vite dev'';
                  depends_on."init frontend".condition = "process_completed";
                };
                backend = {
                  command = ''gradle --continuous :run'';
                  depends_on."init frontend".condition = "process_completed";
                };
              };
            };

            integration-test.processes.integration-test.command = ''nix flake check'';

            lint.processes.lint.command = ''pre-commit run --all-files "''${@}"'';
          };
          composeScripts = scripts:
            pkgs.lib.mapAttrsToList (name: value: let
              cfg = pkgs.writeText "${name}.process-compose.yml" (pkgs.lib.generators.toYAML {} value);
            in
              pkgs.writeShellApplication {
                inherit name;
                text = ''${pkgs.process-compose}/bin/process-compose -t=false -f ${cfg}'';
              })
            scripts;
          updateVerificationMetadata = pkgs.updateVerificationMetadata.override {
            whitelist = [
              # Required by intelliJ, see https://youtrack.jetbrains.com/issue/IDEA-354182/Gradle-dependency-verification-fails-with-kotlin-reflect-1.9.22.pom
              "org.jetbrains.kotlin:kotlin-reflect:1.9.22"
              "org.jetbrains.kotlin:kotlin-stdlib:1.9.22"
            ];
          };
        in
          pkgs.mkShellNoCC {
            inherit (self.checks.${system}.pre-commit-check) shellHook;
            buildInputs = (with pkgs; [jdk nodejs gradle bashInteractive]) ++ [(composeScripts scripts) updateVerificationMetadata];
          };

        formatter = pkgs.alejandra;
      };
    };
}
