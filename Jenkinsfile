pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    triggers {
        githubPush()
    }

    environment {
        APP_NAME = 'locadora-rdt-backend'
        COMPOSE_PROJECT_DIR = '/workspace/locadora-rdt/locadora-rdt-devops'
        GITHUB_REPOSITORY = 'RenAxl/locadora-rdt-backend'
    }

    stages {
        stage('Checkout') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'main'
                }
            }
            steps {
                checkout scm
                sh 'git status --short --branch'
            }
        }

        stage('Maven Clean Package') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'main'
                }
            }
            agent {
                docker {
                    image 'maven:3.8.8-eclipse-temurin-11'
                    args '-v maven_repository:/root/.m2'
                    reuseNode true
                }
            }
            steps {
                sh 'chmod +x ./mvnw'
                sh './mvnw clean package'
            }
            post {
                always {
                    junit allowEmptyResults: false, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Archive JAR') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'main'
                }
            }
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build - Dev') {
            when {
                branch 'dev'
            }
            steps {
                sh '''
                    docker build \
                      -t ${APP_NAME}:dev-latest \
                      -t ${APP_NAME}:latest \
                      .
                '''
            }
        }

        stage('Open Pull Request Dev To Main') {
            when {
                branch 'dev'
            }
            steps {
                withCredentials([string(
                    credentialsId: 'github-token-locadora-rdt-backend',
                    variable: 'GITHUB_TOKEN'
                )]) {
                    sh '''
                        set -e

                        PR_LIST_URL="https://api.github.com/repos/${GITHUB_REPOSITORY}/pulls?state=open&head=RenAxl:dev&base=main"
                        PR_CREATE_URL="https://api.github.com/repos/${GITHUB_REPOSITORY}/pulls"
                        COMPARE_URL="https://api.github.com/repos/${GITHUB_REPOSITORY}/compare/main...dev"

                        curl \
                          -fsS \
                          -H "Authorization: Bearer ${GITHUB_TOKEN}" \
                          -H "Accept: application/vnd.github+json" \
                          -H "X-GitHub-Api-Version: 2022-11-28" \
                          "${PR_LIST_URL}" > open-prs.json

                        if grep -q '"number"' open-prs.json; then
                          echo "Ja existe Pull Request aberto de dev para main."
                          exit 0
                        fi

                        curl \
                          -fsS \
                          -H "Authorization: Bearer ${GITHUB_TOKEN}" \
                          -H "Accept: application/vnd.github+json" \
                          -H "X-GitHub-Api-Version: 2022-11-28" \
                          "${COMPARE_URL}" > compare.json

                        if grep -q '"ahead_by": 0' compare.json; then
                          echo "Branch dev nao possui commits novos para enviar a main. Pull Request nao sera criado."
                          exit 0
                        fi

                        cat > pr-body.json <<EOF
{
  "title": "Merge dev into main - build ${BUILD_NUMBER}",
  "head": "dev",
  "base": "main",
  "body": "Pull Request aberto automaticamente pelo Jenkins apos sucesso da pipeline da branch dev.\\n\\nBuild: ${BUILD_NUMBER}\\nImagem validada: ${APP_NAME}:dev-latest"
}
EOF

                        curl \
                          -fsS \
                          -X POST \
                          -H "Authorization: Bearer ${GITHUB_TOKEN}" \
                          -H "Accept: application/vnd.github+json" \
                          -H "X-GitHub-Api-Version: 2022-11-28" \
                          "${PR_CREATE_URL}" \
                          --data @pr-body.json > created-pr.json

                        echo "Pull Request dev -> main criado com sucesso."
                    '''
                }
            }
        }

        stage('Docker Build - Main') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    docker build \
                      -t ${APP_NAME}:prod-latest \
                      -t ${APP_NAME}:latest \
                      .
                '''
            }
        }

        stage('Deploy Local Backend') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    IMAGE_TAG=prod-latest docker compose \
                      -f ${COMPOSE_PROJECT_DIR}/docker-compose.yml \
                      up -d --no-build backend
                '''
            }
        }

        stage('Backend Health Check') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    for attempt in $(seq 1 30); do
                      if docker run --rm --network locadora-rdt-network curlimages/curl:8.11.1 \
                        -fsS http://backend:8080/actuator/health; then
                        exit 0
                      fi

                      echo "Backend ainda nao esta pronto. Tentativa ${attempt}/30"
                      sleep 5
                    done

                    echo "Backend nao respondeu com sucesso no health check."
                    docker logs --tail 120 locadora-rdt-backend || true
                    exit 1
                '''
            }
        }
    }

    post {
        failure {
            echo 'Pipeline FAILED. Pull Request/deploy nao sera executado apos falha em etapa anterior.'
        }
        success {
            echo 'Pipeline SUCCESS.'
        }
        always {
            cleanWs()
        }
    }
}
