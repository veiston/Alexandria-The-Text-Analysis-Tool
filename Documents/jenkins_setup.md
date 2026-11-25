# Jenkins setup

Use these steps to run the project pipeline in Jenkins.

1. Open Jenkins and create a new **Pipeline** job.
2. In the Pipeline section, choose **Pipeline script from SCM**.
3. Choose **Git** and use the project repository URL.
4. Set the branch to:

   ```text
   */main
   ```

5. Set the script path to:

   ```text
   jenkins/Jenkinsfile
   ```

6. In **Manage Jenkins -> Tools**, add Maven and name it `Maven3`.
7. Install the **HTML Publisher** plugin: open **Manage Jenkins -> Plugins -> Available plugins**, search for `HTML Publisher`, and install it. It is used to show the JaCoCo HTML report in Jenkins.
8. Open the Jenkins job, click **Configure**, and find the **Build Triggers** section. Enable **Poll SCM** and use this schedule:

   ```text
   H * * * *
   ```

   Jenkins will check the repository once every hour.

After a successful build, Jenkins shows JUnit test results and a JaCoCo coverage report.

The GitHub Pages report is published only by Kseniia's Jenkins. Other team members do not need the GitHub token; this stage is skipped in their Jenkins setup for now.

## Windows Jenkins with a local SonarQube

`jenkins/Jenkinsfile.windows` is the Windows version of the pipeline. It uses `bat` instead of `sh`, adds a **SonarQube Analysis** stage for a local SonarQube server (http://localhost:9000) and pushes the image to `luaram/alexandria_luarasilva`.

1. Install the **SonarQube Scanner** plugin.
2. In SonarQube, create a token (**My Account -> Security**). In Jenkins, add it as a **Secret text** credential with the ID `SONAR_TOKEN`.
3. In **Manage Jenkins -> System -> SonarQube servers**, add a server named `SonarQubeServer` with the URL `http://localhost:9000` and the `SONAR_TOKEN` credential.
4. Create a **Pipeline** job with **Pipeline script from SCM**, the project repository URL, branch `*/main` and script path `jenkins/Jenkinsfile.windows`.

The tests need the local MariaDB database (`scripts\setup-db.bat`). To push to another Docker Hub account, change `DOCKERHUB_REPO` and `DOCKERHUB_CREDENTIALS_ID`.
