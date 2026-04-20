package org.entimoss.kuwaiba.provisioning.model.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.entimoss.kuwaiba.provisioning.model.KuwaibaProvisioningRequisition;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

class KuwaibaProvisioningModelTest {

   @Test
   void test() throws StreamReadException, DatabindException, IOException {

      KuwaibaProvisioningRequisition pr = new KuwaibaProvisioningRequisition();

      ObjectMapper om = new ObjectMapper();
      om.enable(SerializationFeature.INDENT_OUTPUT);

      File outputDirectory = new File("./target/external-data");
      System.out.println("output directory: " + outputDirectory.getAbsolutePath());
      outputDirectory.mkdirs();

      File provisioningFile = new File(outputDirectory, "kuwaibaProvisioningRequisition-data.json");
      provisioningFile.delete();

      om.writeValue(provisioningFile, pr);
      System.out.println("Provisioning File saved to: " + provisioningFile.getAbsolutePath());

      File metadataTemplateFile = new File(outputDirectory, "kuwaibaProvisioningRequisition-metadata.json");
      metadataTemplateFile.delete();

      KuwaibaProvisioningRequisition metadataTemplates = new KuwaibaProvisioningRequisition();

      metadataTemplates.setKuwaibaTemplateList(pr.getKuwaibaTemplateList());

      om.writeValue(metadataTemplateFile, metadataTemplates);
      System.out.println("Metadata File saved to: " + metadataTemplateFile.getAbsolutePath());

      // check you can read the file
      KuwaibaProvisioningRequisition pr2 = om.readValue(provisioningFile, KuwaibaProvisioningRequisition.class);
      System.out.println("read file: " + pr2);

   }

}
