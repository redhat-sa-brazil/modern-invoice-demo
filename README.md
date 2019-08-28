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

0. [Pre-Requisities](#deploy-step-0)
1. [Install Red Hat Fuse 7.3 on Openshift](#deploy-step-1)
2. [Install Red Hat Data Grid 7.3 on Openshift](#deploy-step-2)
3. [Deploy Red Hat Data Grid and Red Hat Fusde  Openshift](#deploy-step-3)
4. [Deploy MySQL on Openshift](#deploy-step-4)
5. [Deploy Modern Invoice Demo on Openshift](#deploy-step-5)

### Pre-Requisities <a name="deploy-step-0"/>

* In order to successfully deploy this demo, you're going to need a functional **Openshift 3.11** cluster. This can be obtained using your [on-premise infrastructure](https://docs.openshift.com/container-platform/3.11/install/index.html), traditional Cloud Providers such as [AWS](https://aws.amazon.com/quickstart/architecture/openshift/), [Azure](https://azure.microsoft.com/en-us/services/openshift/), [Google Cloud](https://cloud.google.com/solutions/partners/openshift-on-gcp) and [IBM Cloud](https://cloud.ibm.com/kubernetes/catalog/openshiftcluster), or via [CDK/Minishift](https://developers.redhat.com/products/cdk/overview)

* Setup **Openshift Client 3.11** as describe here: [Installing the CLI](https://docs.openshift.com/container-platform/3.11/cli_reference/get_started_cli.html#installing-the-cli)

* Moving forward, we need to setup **Red Hat Container Registry** integration, which can be done in several ways which are covered in depth on the following references:

  ** [Red Hat Container Registry Authentication](https://access.redhat.com/RegistryAuthentication)
  ** [Setup Registry Authentication](https://access.redhat.com/documentation/en-us/red_hat_data_grid/7.3/html-single/red_hat_data_grid_for_openshift/index#os_registry_authentication)
  ** [Configure Authentication to the Red Hat Container Registry](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.3/html-single/fuse_on_openshift_guide/index#configure-container-registry)
  ** [Minishift Red Hat Registry Login Add-on](https://docs.okd.io/latest/minishift/using/addons.html#default-addons)

* After configuring your **Openshift Cluster** you're going to login as **cluster:admin**. Example:

  ```
  oc login -u system:admin
  ```

### Install Red Hat Fuse 7.3 on Openshift <a name="deploy-step-1"/>

* Installing **Red Hat Fuse** on **Openshift** is very easy. You just need to access to *Openshift* namespace and import Fuse *image streams, quickstarts and templates* just as follows:

  ** Login as **system:admim:**

  ```
  oc login -u system:admin
  ```

  ** Import *Fuse Image Streams:*

  ```
  BASEURL=https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-730065-redhat-00002
  oc create -n openshift -f ${BASEURL}/fis-image-streams.json
  ```

  ** Install quickstart examples and templates for quick reference:

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
  oc create -n openshift -f \
  https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-730065-redhat-00002/quickstarts/${template}
  done
  ```

  ** Finally install **Hawtio Console:**

  ```
  oc create -n openshift -f https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-730065-redhat-00002/fis-console-cluster-template.json
  oc create -n openshift -f https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-730065-redhat-00002/fis-console-namespace-template.json
  ```

### Install Red Hat Data Grid 7.3 on Openshift <a name="deploy-step-2"/>

* **Openshift 3.11** already ships Red Hat Data Grid *cache-service and datagrid-service* templates which are used to deploy this middleware. You can check them by running:

  ```
  oc get templates -n openshift | grep 'cache-service\|datagrid-service'
  ```

* If you can't find *cache-service and datagrid-service* templates, you can easily deploy them by running:

  ```
  oc get templates -n openshift | grep 'cache-service\|datagrid-service'
  ```


### Deploy Red Hat Data Grid and Red Hat Fuse on Openshift <a name="deploy-step-3"/>
### Deploy MySQL on Openshift <a name="deploy-step-4"/>
### Deploy Modern Invoice Demo on Openshift <a name="deploy-step-5"/>

## Additional References <a name="additional-references">
