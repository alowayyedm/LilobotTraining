# A BDI-based Virtual Agent for Training Child Helpline Counsellors
This repository contains three applications. 

1. dktbdiagent - this is the BDI (web) application meant for deployment using Spring Boot. 
<<<<<<< HEAD
2. dktfrontend - this contains a HTML page that makes up the frontend of the application. 
3. dktrasa - this is the Rasa application that handles the intent recognition of the user and retreives a response from the BDI application.

# Instructions
## Rasa (on local machine)
- See Rasa documentation on how to install Rasa on your machine: https://rasa.com/docs/rasa/installation
- Essentially, there are two servers you need to run - the Rasa server for the intent recognition and the custom action server that communicates with the BDI application to retreive a response. 
- To run the custom action server, use ```rasa run actions``` 
- Run the Rasa server using ```rasa run -m models --enable-api --cors  "*"```

## Setting up Rasa Chatbot on Microsoft Azure Server
The following instructions are how I was able to set up Rasa on a virtual machine hosted on Microsoft Azure. 


### Set up virtual machine
1. Create a virtual machine with the following specifications.
- image: Ubuntu 18.04 LTS - Gen 1
- vCPUs: Standard D2s v3 (2 vcpus, 8 GiB memory)
- RAM: 8 GB RAM
- authentication: SSH
- inbound ports: HTTP, HTTPS, SSH
- disk size: 64 GB (100 GB is recommended by Rasa but I made a mistake)

Everything else (networking, management, etc) was left to the default settings.

2. Set up firewall.
3. Set up DNS name. 
By default, Azure generates a dynamic IP address whenever you start your server. It's easier to set up a static one so you can use for connecting to your frontend and other applications.

4. Connect to server via SSH.


### Install Rasa X on server
I partly followed Nelle's instructions (except the part about adding Docker Hub login info. I do this through GitHub Actions. This is explained later.) since I also needed to set up a custom actions server. Here's Rasa official instructions on how to install Rasa X using Docker compose - https://storage.googleapis.com/rasa-x-releases/0.39.1/install.sh.

My Rasa specifications:
- Rasa x version: 0.39.1 (latest version as of writing) -> changed to 0.39.0
- Rasa version: 2.4.0 -> changed to 2.5.0
- Docker version 20.10.6, build 370c289
- docker-compose version 1.26.0, build d4451659
- Rasa webchat version: 1.0.1

I ran into issues later on setting up the frontend so I highly recommend that you check Rasa's compatibility matrix to install the right versions of the packages to avoid problems - https://rasa.com/docs/rasa-x/changelog/compatibility-matrix.


### Connnect Rasa X to Git repository
Additionally, I connected Rasa X to my GitHub repo. This makes it easy to update the Docker container you need for the frontend, etc. Instructions on how to do this are here - https://rasa.com/docs/rasa-x/installation-and-setup/deploy#connect-a-git-repository.


### Connect custom action server
1. First, I had to build an action server image using Docker. Instructions for this are here - https://rasa.com/docs/rasa/how-to-deploy/#building-an-action-server-image. I used the GitHub Actions to automate the image builds instead of setting it up manually using a Dockefile (as done by Nelle). This way, Docker rebuilds the image whenever changes are made to the actions folder and pushed to the main branch. Optionally, you can include automatic upgrades for Rasa X but I didn't do this to avoid breaking things.

2. Optionally, you can add the image to Azure Container Registry - https://azure.microsoft.com/en-us/services/container-registry/
3. Connect the image to Rasa X in your docker-compose.override.yml file - https://rasa.com/docs/rasa-x/installation-and-setup/customize#connecting-a-custom-action-server

```in docker-compose.override.yml:
version: '3.4'
services:
  app:
    image: <username/image:tag>
 ```
 4. If your Docker containers are already running, take them down and then start Rasa X again:

```
cd /etc/rasa
sudo docker-compose down
sudo docker-compose up -d
 ```


## Rasa Webchat (Frontend)
1. I made an HTML file with the script for Rasa webchat as follows. Instructions for Rasa Webchat can be found here -  https://github.com/botfront/rasa-webchat. I used version ```rasa-webchat@1.0.1```  

```
      <script>!(function () {
        let e = document.createElement("script");
          t = document.head || document.getElementsByTagName("head")[0];
        (e.src =
          "https://cdn.jsdelivr.net/npm/rasa-webchat@1.0.1/lib/index.js"),
          // Replace 1.x.x with the version that you want
          (e.async = !0),
          (e.onload = () => {
            window.WebChat.default(
              {
                // initPayload: "request_greeting_unknown",
                customData: { language: "nl" },
                socketUrl: "http://<serverIP>",
                socketPath: "/socket.io/",
                title: "Chat met Lilobot",
                showFullScreenButton: true,
                embedded: false,
                inputTextFieldHint:"Typ een bericht",
                profileAvatar: "images/bot_icon.png",
                docViewer: false,
                params: {
                  storage: "session",
                }
              },
              null
            );
          }),
          t.insertBefore(e, t.firstChild);
        })();
      </script>
```

2. I created a Dockerfile in the same directory as my html file. 

```
FROM nginx:alpine
COPY . /usr/share/nginx/html

```

3. Build using docker as follows:
```
docker build . -t <repository>:<version>
```

4. Tag the image and push to Docker hub.
```
docker tag <imageID> <username>/<repository>:<version>
docker push <username>/<repository>:<version>
```

5. On my remote server, I pulled the image from Docker Hub (I'm already logged into Docker) and ran the container using the commands,
```
docker pull <username>/<repository>:<version>
sudo docker run -d -p 5009:80 <username>/<repository>:<version>
```
Remember to open the inbound port of the VM in Azure to be able to see the webpage. You can use this command if you have Azure CLI installed on your local machine: 
```
az vm open-port --resource-group <resourceGroupName> --name <vmName> --port 5009
```

Note: You might have to start this container on starting up your server.



## Setting up BDI (Spring Boot) application on Microsoft Azure
### Local
 1. Build the JAR file of application and run locally for testing. Make sure you have Postgres set up already so the application doesn't crash. Instructions for this are down below.

### Configure and deploy the app to Azure Spring Cloud
There are two options for running a Spring boot application easily on Azure. One is using an Azure Spring Cloud cluster and the other is using an Azure App Service. The former is more expensive and since the service was not hosting that many users, I chose the second option. 

1. Provision an Azure App Service through the Azure portal. I used Github Actions to build and manage the deployment of the web app. More information here: https://docs.github.com/en/actions/guides/deploying-to-azure-app-service

I used Github Secrets to manage the login credentials to the Azure service to make updates easy. 

### Configure Blob Storage for reports (for remote server only, otherwise use local file system)
1. The BDI agent creates a Word document (.docx) with the BDI status and transcript of the conversation. I store these files on Azure Blob Storage. Follow the instructions below to create and connect the storage to the app service.

https://docs.microsoft.com/en-us/azure/storage/blobs/storage-quickstart-blobs-java?tabs=powershell#configure-your-storage-connection-string

2. I ran into issues trying to get the Rasa frontend to download the file. This works however is the server is hosted locally. Here are the instructions for that.
https://docs.microsoft.com/en-us/azure/storage/blobs/storage-quickstart-blobs-java?tabs=powershell#download-blobs

Instead, I generate SAS tokens for the user to access the file directly from Azure. There is no need for the Rasa frontend to download it anymore. Here are the instructions for that: https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/storage/azure-storage-blob#generate-a-sas-token. 

3. Include the keys to the ReportService of the BDI application.


## Setting up Postgres database
### on local machine
1. Install a postgres server on your local machine. You can find instructions for this through a Google search.  
2. Create database for BDI application.
```
CREATE DATABASE dktbase;
CREATE USER postgres WITH PASSWORD 'postgres';
=======
2. dktfrontend - this contains the Vue.js frontend application.
3. dktrasa - this is the Rasa application that handles the intent recognition of the user's messages.

# Instructions
This application can be run with Docker, or each component can be run individually. 
We recommend first trying to run the application through Docker, although this may be quite slow on your system. 
See how to run the application with Docker [here.](#running-the-project-with-docker) If you intend to host the application on a server, then ensure that the server's firewall is configured correctly [here.](#configure-firewall-on-the-server)

## Getting the repo on the server:
Follow these steps if you are attempting to setup the application on a server.
https://docs.gitlab.com/ee/gitlab-basics/start-using-git.html

Alternatively, you can use personal access tokens to clone the repo.
1. First, create a personal access token at: `https://<your-gitlab-domain>/-/profile/personal_access_tokens`
2. Clone the git repo with `git clone https://<username>:<access-token>@<your-gitlab-domain>/<project-name>.git`

## Setting up Postgres database
### on local machine
1. Install a postgres server on your local machine. You can find instructions for this through a Google search.
2. Create database for BDI application.
```
CREATE DATABASE dktbase;
CREATE USER postgres WITH PASSWORD 'password';
>>>>>>> origin/updatedLilo
GRANT ALL PRIVILEGES ON DATABASE "dktbase" TO postgres;
 ```

3. Include the credentials of your database in the BDI application (in application.properties). The BDI application requires this, otherwise the application will crash.
```
spring.datasource.url=jdbc:postgresql://localhost:5432/dktbase
spring.datasource.username=postgres
<<<<<<< HEAD
spring.datasource.password=postgres
```
You should be able to run your BDI application successfully now. 

### on Microsoft Azure
1. Configure a server instance (Azure Database for PostgreSQL server) on Azure.
2. Configure firewall. I set mine to accept traffic from my personal computer (for debugging purposes) and the Spring application (which should be the only one talking to the database). Make sure to include *all* the outbound IP addresses from the Azure web app.
3. Connect to the server using a postgres client to make sure everything works. I use psql. 

    ```psql "host=<host url> port=5432 dbname=postgres user=<user> password=<password> sslmode=require"```

4. Create database for BDI spring application and configure access.
=======
spring.datasource.password=password
```
You should be able to run your BDI application successfully now.

### On server
1. Configure a server instance.
2. Configure the [server firewall correctly.](#configure-firewall-on-the-server)
3. Install postgres. You can find instructions for this through a Google search.
   - This can be done by using the following commands, but it might differ for each case
   - `apt update` (ensures the server’s local package index is up-to-date)
   - ` apt install postgresql postgresql-contrib` (installs postgresql)
   - `systemctl start postgresql.service` (ensures the postgresql service is started)
   - `service postgresql status` (checks the status of the service, should display as being active)
4. Connect to the server using a postgres client to make sure everything works. I use psql.

   ```psql "host=<host url> port=5432 dbname=postgres user=<user> password=<password> sslmode=require"```

5. Create database for BDI spring application and configure access.
>>>>>>> origin/updatedLilo

```
CREATE DATABASE dktbase;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE "dktbase" TO postgres;
 ```
<<<<<<< HEAD
 
5. Optionally, you can create a database for Rasa event broker to store conversations (with id, sender_id, data) directly from Rasa. I ended up forgoing this because I created a report service in my BDI application that stores the conversation instead. 

```
CREATE DATABASE rasabase;
CREATE USER rasa WITH PASSWORD 'rasa';
GRANT ALL PRIVILEGES ON DATABASE "rasabase" TO rasa;
 ```

More information here: https://github.com/MicrosoftDocs/azure-docs/blob/master/articles/postgresql/quickstart-create-server-database-portal.md

6. Include the credentials of your database in the BDI application (in application.properties) in your Github repository. 
```
spring.datasource.url=jdbc:postgresql://<server-name>:5432/dktbase
spring.datasource.username=postgres
spring.datasource.password=postgres
```
You should be able to run your BDI application successfully now. A more secure option is to use Github password manager (or whatever software management tool you're using) to save the credentials instead so your password isn't exposed.

# Resources
=======

6. Include the credentials of your database in the BDI application (in application.properties) in your Github repository.
```
spring.datasource.url=jdbc:postgresql://<server-name>:5432/dktbase
spring.datasource.username=postgres
spring.datasource.password=password
```
You should be able to run your BDI application successfully now. A more secure option is to use Github password manager (or whatever software management tool you're using) to save the credentials instead so your password isn't exposed.


## Rasa
### Local
#### Installing Rasa
1. Install anaconda locally if you do not already have it: https://docs.anaconda.com/free/anaconda/install/index.html
2. Install python locally if you do not already have it. We used python 3.10: https://www.python.org/downloads/ 
3. Create a new conda environment for rasa in the anaconda terminal.
   - `conda create --name rasa`
   - `conda activate rasa` to activate this environment
4. See Rasa documentation on how to install Rasa on your machine or follow the instructions below: https://rasa.com/docs/rasa/installation/installing-rasa-open-source/
   - `pip3 install -U pip`
   - `pip3 install rasa`
   - `pip3 install rasa[spacy]`
   - `python3 -m spacy download nl_core_news_lg`
   - If these commands do not work, try with `pip` and `python`.

#### Running Rasa
- Essentially, there are two servers you need to run - the Rasa server for the intent recognition and the custom action server that communicates with the BDI application to retreive a response.
- In two separate anaconda terminals, activate the rasa environment with ```conda activate rasa```
- First CD into the ```/dktrasa``` folder
- To run the custom action server, use ```rasa run actions``` 
- Run the Rasa server in the other terminal using ```rasa run -m models --enable-api --cors  "*"```

### On Server
The following instructions describe how to run Rasa on the server.

#### System Specs
Below are the specs of the server that this was run on (with higher system load it is recommended to upgrade this):
  - Intel(R) Xeon(R) Gold 6148 CPU @ 2.40GHz
  - 4 GB System Memory
  - 47 GB Sysem Storage
  - Ubuntu 22.04.1 LTS

Some addresses need to be changed within the project to run the system on the server.
Navigate to `/dktfrontend/training-system-frontend/src/config.js` and change:
```js
const config = {
    agentServer: 'http://localhost:8080',
    agentWsServer: 'ws://localhost:8080/session',
    rasaServer: 'http://localhost:5005'
};
```
to:
```js
const config = {
    agentServer: 'http://{your.server.address}:8080',
    agentWsServer: 'ws://{your.server.address}:8080/session',
    rasaServer: 'http://{your.server.address}:5005'
};
```


1. Connect to server via SSH.
2. Install miniconda on the server by following these instructions: https://docs.conda.io/projects/conda/en/latest/user-guide/install/linux.html
3. Install python with `sudo apt-get install python3.10`
4. Follow the instructions above for installing rasa at [installing rasa](#installing-rasa) from step 3
5. Run rasa by running the commands below in the `/dktrasa` folder:
   - To run the custom action server, use ```nohup rasa run actions &```
   - Run the Rasa server using ```nohup rasa run -m models --enable-api --cors  "*" &```
   - `nohup` ensures that the process does not die when leaving the ssh, and `&` sends the process to the background


## Frontend Web Application
### Local
1. Install npm at https://docs.npmjs.com/downloading-and-installing-node-js-and-npm
2. CD to `/dktfrontend/training-system-frontend`
3. Run `npm install`
4. Run `npm run serve`

The web app will be available at `http://localhost:5601`

### On Server
First, ensure that the frontend configuration is correct. 
Navigate to `/dktfrontend/training-system-frontend/src/config.js` and replace localhost with the server address.
1. Install Nginx:
   - `sudo apt-get update`
   - `sudo apt-get install nginx`
2. Change the line that starts with `root` in `/etc/nginx/sites-available/default` to:
   - `root /<path-to-repo>/dktfrontend/training-system-frontend/dist;`
   - This can be achieved with `sudo nano /etc/nginx/sites-available/default` to edit the file
3. Install nodejs and npm with:
   - `sudo apt-get update`
   - `sudo apt-get install nodejs npm`
4. Navigate to `/<path-to-repo>/dktfrontend/training-system-frontend/`
   - `sudo npm install`
   - `sudo npm run build`
5. Start Nginx with: `sudo systemctl start nginx`

The frontend should be available at `http://<your-server-address>`


## Setting up BDI (Spring Boot) application
### Local
1. Open `/dktbdiagent` with an IDE of your choice as a Maven project. We used IntelliJ.
2. Ensure the postgres DB is running (instructions below).
3. Open `/dktbdiagent/src/main/java/com/bdi/agent/AgentApplication.java` and run the main method.

### On Server 
To run the java application on the server, a change must first be made in `src/main/java/com/bdi/agent/api/WebSocketConfig.java`.

The `registerStompEndpoints` method needs to be changed to:
```
@Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/session")
                .setAllowedOrigins("http://lilobottest.ewi.tudelft.nl);
    }
```

1. Install Maven with:
   - `sudo apt-get update`
   - `sudo apt-get install maven -y`
2. Install Java 16 with:
   - `sudo apt-get update`
   - `sudo apt install oracle-java16-installer`
3. Navigate to `/dktbdiagent`
4. Build the project with: `sudo mvn clean package`
5. Run the project with `nohup java -jar target/agent-0.0.1-SNAPSHOT.jar &`

# Configure firewall on the server
Each component of the application as their own associated port. 
You must ensure that the required ports are opened to allow for necessary communication between components and also between users and the components.
Below is a list of the components and their respective ports.
If desired the associated ports can be modified by changing the corresponding config files of the component (this is not recommended).
- Rasa server: port 5005
- Rasa actions server: port 5055
- Postgres database: port 5432
- Spring application: port 8080
- Vue.js frontend application: port 5601 if running locally and port 80 if running on the server

To configure the firewall correctly, the ports for the rasa server, spring application and frontend application must be opened.
1. Use `ufw status` or `ufw status verbose` to check the status of the firewall. You can run this command after each step to ensure that it was completed correctly.
2. `ufw show added` displays what rules were added to the firewall so far. You can also use this command after each step to ensure that it was completed correctly.
3. If you connect to the server via SSH, open the port used for it `ufw allow ssh`.
4. Enable the firewall `ufw enable`
5. Open all required ports (if the application configuration has not been changed then the required ports are: 5005, 8080 and 80).
Use `ufw allow <port-number>` to fully open the entered port. 
If allowing full access to the port is not desired, then the previous command can be extended to only allow access from specific locations. However, you must ensure that the required components can still access those ports.

# Running the project with Docker

The entire application with all components can also be run together with Docker. This will do the setup for you. 

Beware: the application can run much slower using Docker for Windows. 
To improve performance the application can be run directly in Ubuntu. See how to install Ubuntu for windows at https://ubuntu.com/tutorials/install-ubuntu-on-wsl2-on-windows-10#3-download-ubuntu

To be able to use the application with Docker, some addresses in the repo need to be changed.
 - In `dktbdiagent/src/main/resources/application.properties`\
Change first line:\
`spring.datasource.url=jdbc:postgresql://localhost:5432/dktbase`\
to:\
`spring.datasource.url=jdbc:postgresql://db:5432/dktbase`

 - In `dktrasa/endpoints.yml`\
Change:\
`url: "http://localhost:5055/webhook"` \
to:\
`url: "http://action-server:5055/webhook"`

- In `dktrasa/actions/actions.py`\
Change:\
`BDIAGENT_ENDPOINT = "http://localhost:8080/agent/"` \
`REPORT_ENDPOINT = "http://localhost:8080/report/"` \
to:\
`BDIAGENT_ENDPOINT = "http://dktbdiagent:8080/agent/"` \
`REPORT_ENDPOINT = "http://dktbdiagent:8080/report/"` 

1. Install docker desktop at https://docs.docker.com/engine/install/
2. Open command prompt and navigate to the project repo top directory. 
3. Run the command `docker-compose up`. This may take 5-30 minutes to build.


# Resources (From original Master's thesis project)
>>>>>>> origin/updatedLilo
Links to data related to this thesis. 
- Experiment data (4TU.ResearchData): https://doi.org/10.4121/17371919
- OSF form: Evaluation of a BDI-based virtual agent for training child helpline counsellors - https://osf.io/hkxzc
- Project storage TU Delft: U:\MScProjectDKT (owned by Merijn Bruijnes)
- Thesis report: http://resolver.tudelft.nl/uuid:f04f8f0b-9ab9-4f1c-a19c-43b164d45cce



Here are some handy links I used throughout the thesis. 
- Data analysis markdown file (Willem-Paul):  https://data.4tu.nl/repository/uuid:0cf03876-0f94-4225-b211-c5971d250002
- Data management plan: https://dmponline.tudelft.nl
- Data science research lectures (Willem-Paul): http://yukon.twi.tudelft.nl/weblectures/cs4125.html 
- De Kindertelefoon e-learning: https://www.linkidstudy.nl
- Human research ethics committee (HREC): https://www.tudelft.nl/over-tu-delft/strategie/integriteitsbeleid/human-research-ethics
  - HREC application: https://labservant.tudelft.nl/
  - Template Informed Consent Form: https://www.tudelft.nl/over-tu-delft/strategie/integriteitsbeleid/human-research-ethics/template-informed-consent-form
- Qualtrics TU Delft: https://tudelft.eu.qualtrics.com/
- OSF form: https://osf.io
  - Computer-based intervention for supporting individuals in changing negative thinking patterns: https://osf.io/v6tkq
  - A support system for people with social diabetes distress: https://osf.io/yb6vg
  - Study on effects of a virtual reality exposure with eye-gaze adaptive virtual cognitions: https://osf.io/q58v4
- Rasa: https://rasa.com
- Remote desktop (weblogin) TU Delft: https://weblogin.tudelft.nl/
- Self service portal TU Delft: https://tudelft.topdesk.net
- Transtheoretical model: https://en.wikipedia.org/wiki/Transtheoretical_model
- Virtual human toolkit: https://vhtoolkit.ict.usc.edu
- System Usability Scale: https://www.usability.gov/how-to-and-tools/methods/system-usability-scale.html
  - SUS in Dutch: https://www.usersense.nl/usability-testing/system-usability-scale-sus

<<<<<<< HEAD

# Contact
Feel free to contact me (afua.grundmann@gmail.com) if you have any questions about this project.
=======
  
>>>>>>> origin/updatedLilo
