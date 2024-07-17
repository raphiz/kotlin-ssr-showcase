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
      dependencyFilter = depSpec:
      # kotlin-result-metadata-x.y.z.jar is not uploaded to m2...
        !(
          depSpec.component.group
          == "com.michael-bull.kotlin-result"
          && depSpec.component.name == "kotlin-result"
          && lib.strings.match "^kotlin-result-metadata-[0-9]+\.[0-9]+\.[0-9]+\.jar$" depSpec.name != null
        );
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
