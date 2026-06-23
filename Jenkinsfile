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
        PROD_IMAGE_TAG = "prod-${env.BUILD_NUMBER}"
        COMPOSE_PROJECT_DIR = '/workspace/locadora-rdt/locadora-rdt-devops'
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

        stage('Testes Automatizados') {
            when {
                branch 'dev'
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
                sh './mvnw verify'
            }
            post {
                always {
                    junit allowEmptyResults: false, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube') {
            when {
                branch 'dev'
            }
            agent {
                docker {
                    image 'maven:3.9.9-eclipse-temurin-17'
                    args '-v maven_repository:/root/.m2 --network locadora-rdt-network'
                    reuseNode true
                }
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw sonar:sonar -Dsonar.projectKey=locadora-rdt-backend -Dsonar.projectName=locadora-rdt-backend -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml'
                }
            }
        }

        stage('Quality Gate') {
            when {
                branch 'dev'
            }
            agent {
                docker {
                    image 'curlimages/curl:8.11.1'
                    args '--network locadora-rdt-network'
                    reuseNode true
                }
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        set -eu

                        if [ ! -f target/sonar/report-task.txt ]; then
                          echo "Arquivo target/sonar/report-task.txt nao encontrado. A analise SonarQube nao foi executada corretamente."
                          exit 1
                        fi

                        ce_task_url="$(grep '^ceTaskUrl=' target/sonar/report-task.txt | cut -d= -f2-)"

                        auth_args=""
                        if [ -n "${SONAR_AUTH_TOKEN:-}" ]; then
                          auth_args="-u ${SONAR_AUTH_TOKEN}:"
                        fi

                        analysis_id=""

                        for attempt in $(seq 1 60); do
                          ce_response="$(curl -fsS ${auth_args} "${ce_task_url}")"
                          ce_status="$(printf '%s' "${ce_response}" | sed -n 's/.*"status":"\\([^"]*\\)".*/\\1/p')"

                          echo "SonarQube Compute Engine status: ${ce_status} (${attempt}/60)"

                          if [ "${ce_status}" = "SUCCESS" ]; then
                            analysis_id="$(printf '%s' "${ce_response}" | sed -n 's/.*"analysisId":"\\([^"]*\\)".*/\\1/p')"
                            break
                          fi

                          if [ "${ce_status}" = "FAILED" ] || [ "${ce_status}" = "CANCELED" ]; then
                            echo "Processamento da analise SonarQube falhou: ${ce_status}"
                            exit 1
                          fi

                          sleep 5
                        done

                        if [ -z "${analysis_id}" ]; then
                          echo "Timeout aguardando processamento da analise SonarQube."
                          exit 1
                        fi

                        qg_response="$(curl -fsS ${auth_args} "${SONAR_HOST_URL}/api/qualitygates/project_status?analysisId=${analysis_id}")"
                        qg_status="$(printf '%s' "${qg_response}" | sed -n 's/.*"status":"\\([^"]*\\)".*/\\1/p')"
                        coverage_condition="$(printf '%s' "${qg_response}" | tr '{' '\\n' | grep '"metricKey":"new_coverage"' || true)"
                        duplication_condition="$(printf '%s' "${qg_response}" | tr '{' '\\n' | grep '"metricKey":"new_duplicated_lines_density"' || true)"

                        coverage_status="$(printf '%s' "${coverage_condition}" | sed -n 's/.*"status":"\\([^"]*\\)".*/\\1/p')"
                        duplication_status="$(printf '%s' "${duplication_condition}" | sed -n 's/.*"status":"\\([^"]*\\)".*/\\1/p')"
                        coverage_value="$(printf '%s' "${coverage_condition}" | sed -n 's/.*"actualValue":"\\([^"]*\\)".*/\\1/p')"
                        duplication_value="$(printf '%s' "${duplication_condition}" | sed -n 's/.*"actualValue":"\\([^"]*\\)".*/\\1/p')"

                        echo "SonarQube Quality Gate status geral: ${qg_status}"
                        echo "New Code Coverage: ${coverage_value:-N/A}% (status: ${coverage_status:-MISSING}, minimo: 80%)"
                        echo "New Duplicated Lines: ${duplication_value:-N/A}% (status: ${duplication_status:-MISSING}, maximo: 3%)"

                        if [ "${coverage_status}" != "OK" ]; then
                          echo "Quality Gate reprovado: New Code Coverage deve ser >= 80%."
                          printf '%s\\n' "${qg_response}"
                          exit 1
                        fi

                        if [ "${duplication_status}" != "OK" ]; then
                          echo "Quality Gate reprovado: New Duplicated Lines deve ser <= 3%."
                          printf '%s\\n' "${qg_response}"
                          exit 1
                        fi

                        echo "Quality Gate aprovado para as metricas configuradas na pipeline."
                    '''
                }
            }
        }

        stage('Archive JAR') {
            when {
                branch 'main'
            }
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build - Main') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    docker build \
                      -t ${APP_NAME}:${PROD_IMAGE_TAG} \
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
                    IMAGE_TAG=${PROD_IMAGE_TAG} docker compose \
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
            echo 'Pipeline FAILED. Etapas posteriores nao serao executadas apos falha anterior.'
        }
        success {
            echo 'Pipeline SUCCESS.'
        }
        always {
            cleanWs()
        }
    }
}
