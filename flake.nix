{
  description = "Hyperskill Blockchain - Java 17 development environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = {
    self,
    nixpkgs,
    flake-utils,
  }:
    flake-utils.lib.eachDefaultSystem (system: let
      pkgs = nixpkgs.legacyPackages.${system};
    in {
      devShells.default = pkgs.mkShell {
        buildInputs = with pkgs; [
          zulu17
          gradle
          kotlin
        ];

        shellHook = ''
          export JAVA_HOME="${pkgs.zulu17.home}"
          echo "Java 17 development environment"
          java -version
        '';
      };
    });
}
