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
    version = self.shortRev or "dirty";
  in
    flake-parts.lib.mkFlake {inherit inputs;} {
      systems = nixpkgs.lib.systems.flakeExposed;
      flake = {
        overlays.default = final: prev: {
          jdk = prev.jdk22_headless;
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
            };
          };
        };
        devShells.default = pkgs.mkShellNoCC {
          inherit (self.checks.${system}.pre-commit-check) shellHook;
          buildInputs = with pkgs; [jdk gradle updateVerificationMetadata];
        };

        formatter = pkgs.alejandra;
      };
    };
}
