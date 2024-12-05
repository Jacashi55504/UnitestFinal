version: 2.1

executors:
  java-executor:
    docker:
      - image: circleci/openjdk:11  # Java 11
      - image: mysql:8.0  # Contenedor MySQL para pruebas de integración
        environment:
          MYSQL_ROOT_PASSWORD: root
          MYSQL_DATABASE: calidad2024

jobs:
  unit_tests:
    executor: java-executor
    steps:
      - checkout

      # Instalar Maven (si no está preinstalado)
      - run:
          name: Install Maven
          command: sudo apt-get update && sudo apt-get install -y maven

      # Compilar y ejecutar pruebas unitarias
      - run:
          name: Run Unit Tests
          command: mvn clean test -Dtest=!UserDAOTest

  integration_tests:
    executor: java-executor
    steps:
      - checkout

      # Asegurarse de que MySQL esté listo antes de las pruebas
      - run:
          name: Wait for MySQL
          command: |
            for i in {1..30}; do
              nc -z localhost 3306 && echo "MySQL is up!" && break
              echo "Waiting for MySQL..."
              sleep 1
            done

      # Compilar el proyecto y ejecutar pruebas de integración
      - run:
          name: Run Integration Tests
          command: mvn clean test -Dtest=UserDAOTest

  functional_tests:
    executor: java-executor
    steps:
      - checkout

      # Instalar Google Chrome
      - run:
          name: Install Google Chrome
          command: |
            wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | sudo apt-key add -
            echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" | sudo tee /etc/apt/sources.list.d/google-chrome.list
            sudo apt-get update
            sudo apt-get install -y google-chrome-stable

      # Descargar y configurar ChromeDriver
      - run:
          name: Install ChromeDriver
          command: |
            CHROME_DRIVER_VERSION=$(wget -qO- https://chromedriver.storage.googleapis.com/LATEST_RELEASE)
            wget -N https://chromedriver.storage.googleapis.com/$CHROME_DRIVER_VERSION/chromedriver_linux64.zip
            unzip chromedriver_linux64.zip
            sudo mv chromedriver /usr/local/bin/
            sudo chmod +x /usr/local/bin/chromedriver

      # Ejecutar pruebas funcionales con Selenium
      - run:
          name: Run Functional Tests
          command: mvn clean test -Dtest=CRUDSeleniumTest

workflows:
  version: 2
  build_and_test:
    jobs:
      - unit_tests
      - integration_tests:
          requires:
            - unit_tests
      - functional_tests
