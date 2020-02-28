@Library('jenkins-pipeline') _

node {
  cleanWs()

  try {
    dir('src') {
      stage('SCM') {
        checkout scm
      }

      if (getGitCommitAuthorEmail().contains('build')) {
        /*
         * Do not do builds on top of Jenkins commits
         */
        return
      }

      updateGithubCommitStatus('PENDING', "${env.WORKSPACE}/src")
      stage('test') {
        test()
      }
      if (getGitBranch() == 'master') {
        parallel(
          'deploy': { deploy() },
          'doc': { doc() }
        )
      }
    }
  } catch (err) {
    currentBuild.result = 'FAILURE'
    throw err
  } finally {
    if (!currentBuild.result) {
      currentBuild.result = 'SUCCESS'
    }
    updateGithubCommitStatus(currentBuild.result, "${env.WORKSPACE}/src")
    cleanWs cleanWhenFailure: false
  }
}

def test() {
  docker.withRegistry('https://registry.internal.exoscale.ch') {
    def clojure = docker.image('registry.internal.exoscale.ch/exoscale/clojure:cosmic')
    clojure.pull()

    try {
      clojure.inside('-u root -v /home/exec/.m2/repository:/root/.m2/repository') {
        sh 'lein test'
      }
    } finally {
      junit "target/test-reports/*.xml"
    }
  }
}

def deploy() {
  withCredentials([
      usernamePassword(credentialsId: 'sos-api',
                       usernameVariable: 'AWS_ACCESS_KEY_ID',
                       passwordVariable: 'AWS_SECRET_KEY'),
      usernamePassword(credentialsId: 'github-token',
                       usernameVariable: 'GIT_USERNAME',
                       passwordVariable: 'GIT_PASSWORD')]) {
    docker.withRegistry('https://registry.internal.exoscale.ch') {
      sh 'git config user.email "operation+build@exoscale.net" && git config user.name "Exoscale BuildBot"'
      sh 'git checkout master'
      def clojure = docker.image('registry.internal.exoscale.ch/exoscale/clojure:cosmic')
      clojure.pull()
      clojure.inside('-u root -v /home/exec/.m2/repository:/root/.m2/repository -e AWS_ACCESS_KEY_ID -e AWS_SECRET_KEY -e GIT_USERNAME -e GIT_PASSWORD') {
        sh 'echo "machine github.com\nlogin ${GIT_USERNAME}\npassword ${GIT_PASSWORD}" > ~/.netrc'
        sh 'lein release'
        currentBuild.description = "release build"
      }
    }
  }
}


def doc() {
  publishHTML target: [
    allowMissing: false,
    alwaysLinkToLastBuild: false,
    keepAll: true,
    reportDir: 'target/doc',
    reportFiles: 'index.html',
    reportName: 'clojure-kubectl documentation'
  ]
}
