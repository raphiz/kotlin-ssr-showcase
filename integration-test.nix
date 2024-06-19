{kotlin-ssr-showcase-module}: {pkgs, ...}: let
  serverDomain = "example.local";
  serverPort = 4242;
in {
  name = "Kotlin SSR Showcase Integration test";
  nodes = {
    server = {...}: {
      imports = [kotlin-ssr-showcase-module];

      services.kotlin-ssr-showcase = {
        enable = true;
        port = serverPort;
      };

      networking.hosts."::1" = ["${serverDomain}"];
      networking.firewall.allowedTCPPorts = [serverPort];
    };

    client = {nodes, ...}: {
      networking.hosts."${nodes.server.networking.primaryIPAddress}" = ["${serverDomain}"];
    };
  };

  testScript = ''
    start_all()
    server.wait_for_unit("kotlin-ssr-showcase.service")
    server.wait_until_succeeds("curl -f -L http://${serverDomain}:${toString serverPort}/")
  '';
}
