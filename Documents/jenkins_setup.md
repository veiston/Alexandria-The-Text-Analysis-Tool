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
