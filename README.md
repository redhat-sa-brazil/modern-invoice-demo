# Modern Invoice Demo

This demo demonstrates how to integrate [Red Hat Data Grid](https://www.redhat.com/en/technologies/jboss-middleware/data-grid) and [Red Hat Fuse](https://www.redhat.com/en/technologies/jboss-middleware/fuse) through a simple Credit Card Invoice offload use case.

The [Additional References](#additional-references) section will provide complementary assets for further reading and complementary details about related subjects.

## Description

## Environment

- [Red Hat Openshift Container Platform 3.11](https://docs.openshift.com/container-platform/3.11/welcome/index.html)
- [Red Hat Fuse 7.3](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.3/html-single/fuse_on_openshift_guide/index)
- [Red Hat Data Grid 7.3](https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html-single/red_hat_data_grid_for_openshift/index)
- [Openshift Client 3.11.16](https://github.com/openshift/origin/releases/tag/v3.11.0)

## Deployment on Openshift

0. [Pre-Requisites](#deploy-step-0)
1. [Install Red Hat Fuse 7.3 on Openshift](#deploy-step-1)
2. [Install Red Hat Data Grid 7.3 on Openshift](#deploy-step-2)
3. [Create Modern Invoice Project](#deploy-step-3)
4. [Deploy MySQL](#deploy-step-4)
5. [Deploy Red Hat Data Grid](#deploy-step-5)
6. [Deploy Modern Invoice Demo](#deploy-step-6)

### Pre-Requisities <a name="deploy-step-0"/>

* Deploying **Red Hat Fuse** based can be very easy when using [Apache Maven]() capabilities. Furthermore to build and compile our demo, we're going to need **Red Hat** repositories propertly configured. That said, you can refer the following *settings.xml*:

  ```
  <?xml version="1.0" encoding="UTF-8" standalone="no"?>
  <settings xmlns="http://maven.apache.org/SETTINGS/1.1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd">
  <profiles>
    <profile>
      <id>default</id>
      <repositories>
        <repository>
          <id>maven-central</id>
          <name>Mave Central Repo</name>
          <url>https://repo1.maven.org/maven2</url>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </snapshots>
        </repository>
        <repository>
          <id>redhat-ga</id>
          <name>Red Hat GA</name>
          <url>http://maven.repository.redhat.com/ga/</url>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </snapshots>
        </repository>
        <repository>
          <id>redhat-techpreview</id>
          <name>Red Hat TechPreview</name>
          <url>http://maven.repository.redhat.com/techpreview/all/</url>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </snapshots>
        </repository>
        <repository>
          <id>redhat-earlyaccess</id>
          <name>Red Hat Early Access</name>
          <url>https://maven.repository.redhat.com/earlyaccess/all</url>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </snapshots>
        </repository>
        <repository>
          <id>jboss-ea</id>
          <name>JBoss Early Access Repository</name>
          <url>http://repository.jboss.org/nexus/content/groups/ea</url>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </releases>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>maven-central</id>
          <name>Mave Central Repo</name>
          <url>https://repo1.maven.org/maven2</url>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </snapshots>
        </pluginRepository>
        <pluginRepository>
          <id>redhat-ga</id>
          <name>Red Hat GA</name>
          <url>http://maven.repository.redhat.com/ga/</url>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </snapshots>
        </pluginRepository>
        <pluginRepository>
          <id>redhat-techpreview</id>
          <name>Red Hat TechPreview</name>
          <url>http://maven.repository.redhat.com/techpreview/all/</url>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </snapshots>
        </pluginRepository>
        <pluginRepository>
          <id>redhat-earlyaccess</id>
          <name>Red Hat Early Access</name>
          <url>https://maven.repository.redhat.com/earlyaccess/all</url>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>daily</updatePolicy>
          </snapshots>
        </pluginRepository>
        <pluginRepository>
          <id>jboss-ea</id>
          <name>JBoss Early Access Repository</name>
          <url>http://repository.jboss.org/nexus/content/groups/ea</url>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <releases>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
          </releases>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>

  <activeProfiles>
    <activeProfile>default</activeProfile>
  </activeProfiles>

  </settings>
  ```

* In order to successfully deploy this demo, you're going to need a functional **Openshift 3.11** cluster. This can be obtained using your [on-premise infrastructure](https://docs.openshift.com/container-platform/3.11/install/index.html), traditional Cloud Providers such as [AWS](https://aws.amazon.com/quickstart/architecture/openshift/), [Azure](https://azure.microsoft.com/en-us/services/openshift/), [Google Cloud](https://cloud.google.com/solutions/partners/openshift-on-gcp) and [IBM Cloud](https://cloud.ibm.com/kubernetes/catalog/openshiftcluster), or via [CDK/Minishift](https://developers.redhat.com/products/cdk/overview)

* Setup **Openshift Client 3.11** as describe here: [Installing the CLI](https://docs.openshift.com/container-platform/3.11/cli_reference/get_started_cli.html#installing-the-cli)

* Moving forward, we need to setup **Red Hat Container Registry** integration, which can be done in several ways which are covered in depth on the following references:

  * [Red Hat Container Registry Authentication](https://access.redhat.com/RegistryAuthentication)
  * [Setup Registry Authentication](https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html-single/red_hat_data_grid_for_openshift/index#os_registry_authentication)
  * [Configure Authentication to the Red Hat Container Registry](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.3/html-single/fuse_on_openshift_guide/index#configure-container-registry)
  * [Minishift Red Hat Registry Login Add-on](https://docs.okd.io/latest/minishift/using/addons.html#default-addons)

* After configuring your **Openshift Cluster** you're going to login as **cluster:admin**. Example:

  ```
  oc login -u system:admin
  ```

### Install Red Hat Fuse 7.3 on Openshift <a name="deploy-step-1"/>

* Installing **Red Hat Fuse** on **Openshift** is very easy. You just need to access to *Openshift* namespace and import Fuse *image streams, quickstarts and templates* just as follows:

  * Login as **system:admim:**

  ```
  oc login -u system:admin
  ```

  * Import *Fuse Image Streams:*

  ```
  BASEURL=https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-730065-redhat-00002
  oc replace -n openshift -f ${BASEURL}/fis-image-streams.json
  ```

  * Install quickstart examples and templates for quick reference:

  ```
  for template in eap-camel-amq-template.json \
    eap-camel-cdi-template.json \
    eap-camel-cxf-jaxrs-template.json \
    eap-camel-cxf-jaxws-template.json \
    eap-camel-jpa-template.json \
    karaf-camel-amq-template.json \
    karaf-camel-log-template.json \
    karaf-camel-rest-sql-template.json \
    karaf-cxf-rest-template.json \
    spring-boot-camel-amq-template.json \
    spring-boot-camel-config-template.json \
    spring-boot-camel-drools-template.json \
    spring-boot-camel-infinispan-template.json \
    spring-boot-camel-rest-sql-template.json \
    spring-boot-camel-teiid-template.json \
    spring-boot-camel-template.json \
    spring-boot-camel-xa-template.json \
    spring-boot-camel-xml-template.json \
    spring-boot-cxf-jaxrs-template.json \
    spring-boot-cxf-jaxws-template.json ;
    do
  oc replace -n openshift -f \
  https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-730065-redhat-00002/quickstarts/${template}
  done
  ```

  * Finally install **Hawtio Console:**

  ```
  oc create -n openshift -f https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-730065-redhat-00002/fis-console-cluster-template.json
  oc create -n openshift -f https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-730065-redhat-00002/fis-console-namespace-template.json
  ```

### Install Red Hat Data Grid 7.3 on Openshift <a name="deploy-step-2"/>

* **Openshift 3.11** already ships Red Hat Data Grid *cache-service and datagrid-service* templates which are used to deploy this middleware. You can check them by running:

  ```
  oc get templates -n openshift | grep 'cache-service\|datagrid-service'
  ```

* If you can't find *cache-service and datagrid-service* templates, you can easily deploy them by executing:

  ```
  for resource in cache-service-template.yaml \
  datagrid-service-template.yaml
  do
    oc create -n openshift -f \
      https://raw.githubusercontent.com/jboss-container-images/jboss-datagrid-7-openshift-image/7.3-v1.2/services/${resource}
  done
  ```

### Create Modern Invoice Project <a name="deploy-step-3"/>

* In order to keep our environment organized, we're going to create a separated *project/namespace* to deploy our services and demo. To achieve this, you just need to run:

  ```
  oc new-project invoice
  ```
  * *TIP:* you can choose a different project name if you wish.

### Deploy MySQL <a name="deploy-step-4"/>

* **Openshift** ships several [Databases](https://docs.openshift.com/container-platform/3.11/using_images/db_images/index.html) such as *MongoDB, PostgreSQL* and others. I've chose *MySQL* for this demo, therefore, we need to deploy it as follows:

  ```
  oc new-app --template=mysql-ephemeral --param=MYSQL_USER=admin --param=MYSQL_PASSWORD=admin
  ```
  * *TIP 1:* you can choose different username and password
  * *TIP 2:* take note of these attributes

* Wait a few seconds for *MySQL* deployment and execute *oc get pods* to make sure everything went just fine:

  ```
  > oc get pods
  NAME            READY     STATUS    RESTARTS   AGE
  mysql-1-th7kp   1/1       Running   0          1m
  ```
  * *TIP 1 :* the pod name *mysql-1-th7kp* is going to be different from your;
  * *TIP 2 :* make sure your pod has a **Running** status with a **1/1** count;

### Deploy Red Hat Data Grid <a name="deploy-step-5"/>

* As I previously stated, deploying Red Hat Data Grid is quite straightforward using via *Openshift Templates*. Just execute the following and a RDG instance will be provisioned afterwards:

  ```
  > oc new-app cache-service \
  -p APPLICATION_USER=cache \
  -p APPLICATION_PASSWORD=cache \
  -p APPLICATION_NAME=cache-service
  ```
  * *TIP 1:* you can choose different APPLICATION_USER and APPLICATION_PASSWORD
  * *TIP 2:* take note of these attributes

### Deploy Modern Invoice Demo <a name="deploy-step-6"/>

* Before deploying Modern Invoice Demo, we need to compile it. Thus, navigate to the *source/modern-invoice* directory and execute the following:

  ```
  mvn clean compile

  mvn fabric8:deploy \
  -Dmysql-service-username=admin \
  -Dmysql-service-password=admin \
  -Djdg-application-name=cache-service \
  -Djdg-application-user=cache \
  -Djdg-application-password=cache
  ```
  * *TIP 1:* you can choose different APPLICATION_USER and APPLICATION_PASSWORD
  * *TIP 2:* take note of these attributes

## Additional References <a name="additional-references">
