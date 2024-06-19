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
        overlays.default = final: prev: {
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
      perSystem = {
        config,
        system,
        ...
      }: let
        pkgs = import nixpkgs {
          inherit system;
          overlays = [
            build-gradle-application.overlays.default
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
            buildInputs = with pkgs; [jdk gradle ktlint detekt updateVerificationMetadata autoformat];
          };

        formatter = pkgs.alejandra;
      };
    };
}
