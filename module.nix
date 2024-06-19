{
  config,
  inputs,
  lib,
  pkgs,
  ...
}: let
  cfg = config.services.kotlin-ssr-showcase;
in {
  ###### interface

  options = {
    services.kotlin-ssr-showcase = {
      enable = lib.mkEnableOption (lib.mdDoc "Enable Kotlin SSR Showcase App");

      package = lib.mkOption {
        type = lib.types.package;
        default = pkgs.kotlin-ssr-showcase;
        description = lib.mdDoc ''
          Application package to use.
        '';
      };

      user = lib.mkOption {
        type = lib.types.str;
        default = "kotlin-ssr-showcase";
        description = lib.mdDoc "User account under which the application runs";
      };

      port = lib.mkOption {
        type = lib.types.port;
        default = 8000;
        description = lib.mdDoc ''
          The port on which the application listens.
        '';
      };
    };
  };

  ###### implementation

  config = lib.mkIf cfg.enable {
    users.users.kotlin-ssr-showcase =
      lib.mkIf (cfg.user == "kotlin-ssr-showcase")
      {
        name = "kotlin-ssr-showcase";
        isSystemUser = true;
        group = "kotlin-ssr-showcase";
        description = "kotlin-ssr-showcase server user";
      };
    users.groups.kotlin-ssr-showcase = lib.mkIf (cfg.user == "kotlin-ssr-showcase") {};

    environment.systemPackages = [cfg.package];

    systemd.services.kotlin-ssr-showcase = {
      description = "Kotlin SSR Showcase App";

      wantedBy = ["multi-user.target"];
      after = ["network.target"];
      requires = ["nss-lookup.target" "network-online.target"];
      serviceConfig = {
        ExecStart = "${cfg.package}/bin/kotlin-ssr-showcase";
        User = cfg.user;
        Restart = "always";
        RestartSec = "5s";
        Environment = [
          ''SERVER_PORT=${toString cfg.port}''
        ];
      };
    };
  };
}
