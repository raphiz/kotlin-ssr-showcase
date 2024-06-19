{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-24.05";
    build-gradle-application.url = "github:raphiz/buildGradleApplication";
    pre-commit-hooks.url = "github:cachix/pre-commit-hooks.nix";
  };

  outputs = inputs @ {
    self,
    nixpkgs,
    flake-parts,
    build-gradle-application,
    pre-commit-hooks,
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
            jdk = prev.jdk22_headless;
            jre_headless = prev.jdk22_headless;
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
                  script = pkgs.writeShellScriptBin "detekt-wrapper" ''IFS=','; ${pkgs.detekt}/bin/detekt -i "$*"; unset IFS;'';
                in "${script}/bin/detekt-wrapper";
                files = "\\.(kt|kts)$";
                language = "system";
              };
              ktlint = {
                enable = true;
                name = "ktlint";
                entry = "${pkgs.ktlint}/bin/ktlint";
                files = "\\.(kt|kts)$";
                language = "system";
              };
            };
          };
        };
        devShells.default = let
          build = pkgs.writeShellApplication {
            name = "build";
            text = ''gradle :clean :check :installDist'';
          };
          build-continuously = pkgs.writeShellApplication {
            name = "build-continuously";
            text = ''gradle --continuous :check :installDist'';
          };
          integration-test = pkgs.writeShellApplication {
            name = "integration-test";
            text = ''nix flake check'';
          };
          rundev = pkgs.writeShellApplication {
            name = "rundev";
            text = ''gradle :run'';
          };
          autoformat = pkgs.writeShellApplication {
            name = "autoformat";
            text = ''
              ${pkgs.alejandra}/bin/alejandra .
              ${pkgs.ktlint}/bin/ktlint --format
              ${pkgs.detekt}/bin/detekt --auto-correct
            '';
          };
        in
          pkgs.mkShellNoCC {
            inherit (self.checks.${system}.pre-commit-check) shellHook;
            buildInputs = with pkgs; [jdk gradle ktlint detekt updateVerificationMetadata build build-continuously integration-test rundev autoformat];
          };

        formatter = pkgs.alejandra;
      };
    };
}
