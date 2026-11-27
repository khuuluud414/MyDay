pipeline {
  agent any
  environment {
    IMAGE = "dockerkhuuluud/myday"
    TAG = "1.0"
    JAVA_HOME = "C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.x-hotspot"
  }
  triggers { pollSCM('H/2 * * * *') }
  stages {
    stage('Checkout') { steps { checkout scm } }

    stage('Build & Tests') {
      steps { bat 'mvn clean package' }
      post { always { junit 'target/surefire-reports/*.xml' } }
    }

    stage('SonarQube') {
      steps {
        withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
          bat 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=MyDay -Dsonar.host.url=http://localhost:9000 -Dsonar.token=%SONAR_TOKEN% -Dsonar.qualitygate.wait=true'
        }
      }
    }

    stage('Docker Build') {
      steps { bat 'docker build -t %IMAGE%:%TAG% .' }
    }

    stage('Trivy') {
      steps {
        bat 'docker run --rm -v //var/run/docker.sock:/var/run/docker.sock aquasec/trivy:latest image --severity HIGH,CRITICAL --exit-code 0 %IMAGE%:%TAG%'
      }
    }

    stage('Push Docker Hub') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'U', passwordVariable: 'P')]) {
          bat 'docker login -u %U% -p %P%'
          bat 'docker push %IMAGE%:%TAG%'
        }
      }
    }

    stage('Deploy K8s') {
      steps {
        bat 'docker save %IMAGE%:%TAG% | docker exec -i k3s ctr images import -'
        bat 'kubectl apply -f k8s/'
        bat 'kubectl rollout restart deployment myday'
        bat 'kubectl rollout status deployment myday --timeout=120s'
      }
    }
  }
}