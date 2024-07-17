{
  lib,
  version,
  buildGradleApplication,
  buildNpmPackage,
}: let
  frontend =
    (buildNpmPackage {
      src = ./.; # TODO: Filter!
      npmBuild = "npm run build";
    })
    .overrideAttrs {
      installPhase = ''
        runHook preInstall
        mv dist $out
        runHook postInstall
      '';
    };
in
  (buildGradleApplication
    {
      pname = "kotlin-ssr-showcase";
      version = version;
      buildTask = ":check :installDist";
      src = ./.; # TODO: Filter ci config, docs, nix tests etc.
      meta = {
        description = "Kotlin SSR Showcase";
        maintainers = [
          {
            email = "hi@raphael.li";
            github = "raphiz";
            githubId = 605630;
            name = "Raphael Zimmermann";
          }
        ];
        sourceProvenance = with lib.sourceTypes; [
          fromSource
          binaryBytecode
        ];
        license = lib.licenses.mit;
      };
    })
  .overrideAttrs {
    preBuild = ''
      mkdir -p build/resources
      ln -s ${frontend} build/resources/assets
      ls -al build/resources/assets
    '';
  }
