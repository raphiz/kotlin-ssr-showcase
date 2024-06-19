{
  lib,
  version,
  buildGradleApplication,
}:
buildGradleApplication
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
}
