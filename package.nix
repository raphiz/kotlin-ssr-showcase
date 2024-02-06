{
  lib,
  version,
  buildGradleApplication,
}:
buildGradleApplication
{
  pname = "kotlin-ssr-showcase";
  version = version;
  src = ./.;
  meta = with lib; {
    description = "Kotlin SSR Showcase";
    maintainers = [
      {
        email = "hi@raphael.li";
        github = "raphiz";
        githubId = 605630;
        name = "Raphael Zimmermann";
      }
    ];
    sourceProvenance = with sourceTypes; [
      fromSource
      binaryBytecode
    ];
    license = licenses.mit;
  };
}
