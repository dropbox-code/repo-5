[![Build Status](https://travis-ci.org/Contrast-Security-OSS/ide-plugin-eclipse2.svg?branch=master)](https://travis-ci.org/Contrast-Security-OSS/ide-plugin-eclipse2)

# IDE plugin connecting to REST API

### Getting Started

- Install Java JDK (minimal version 7) and maven 3.3.x
- clone repository
- cd ide-plugin-eclipse2
- call

- cd targets; mvn clean install
- cd ..; mvn clean install -Djarsigner.skip=true 
- mvn clean verify

- start Eclipse (current target version is Mars, but I have tested with Neon)
- select Help>Install New Software>Add
- click Archive...
- select updatesite/target/contrastide.updatesite-1.0.0-SNAPSHOT.zip
- select Contrast IDE
- restart Eclipse

## Testing

Project is currently configured to run tests with maven from project source using maven goal **integration-test**. This will 
run unit and integration tests together. In order to run successfully integration tests, it is required to pass some values as
maven parameters:

- username
- apiKey
- serviceKey
- restApiUrl
- organizationId

Example command: 


    mvn clean integration-test -Dusername=someone -DapiKey=youApiKeyForOrganization -DserviceKey=yourServiceKey -DrestApiUrl=tsApiUrl -DorganizationId=orgId1

### Development

Requirements: Eclipse SDK Mars 4.5 or higher, recommended m2e and EGit

- import all "Existing Projects"
- create an Eclipse PDE Run/Debug configuration with included com.contrastsecurity.ide* plugins

### Signing

Plugin build makes use of [Apache Maven Jarsigner Plugin](http://maven.apache.org/plugins/maven-jarsigner-plugin/) to sign automatically during package phase.
Sign required parameters are passed as maven build parameters as follow:

* keystore.path : Path location of the keystore file (.jks).
* keystore.storepass : Keystore password.
* keystore.alias : Key alias.
* keystore.keypass : Key password.

Example maven command:

    mvn clean install -Dkeystore.path=/path/to/keystore -Dkeystore.storepass=keystorePass -Dkeystore.alias=alias -Dkeystore.keypass=keyPass

To disable this behavior add this line at the end of your maven build command:

    mvn ... -Djarsigner.skip=true

### Releasing a new version
* [unleash-maven-plugin](https://github.com/shillner/unleash-maven-plugin) is used for releases.
* To start a new release, run the following command. It requires github username and the name of system environment variable containing the github password. It uses the standard plugin release workflow for [unleash:perform-tycho](https://github.com/shillner/unleash-maven-plugin/wiki/unleash:perform-tycho) goal, but it does not deploy artifacts.

    ```mvn clean unleash:perform-tycho -Dworkflow=customWorkflow -Dunleash.releaseArgs="jarsigner.skip=true" -Dunleash.scmUsername="USERNAME" -Dunleash.scmPasswordEnvVar="GITHUB_PASS_ENV_VAR"```

### License
GPL version 3 
