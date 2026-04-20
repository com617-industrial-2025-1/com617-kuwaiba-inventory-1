package org.entimoss.kuwaiba.provisioning.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class KuwaibaProvisioningRequisition {

   private List<KuwaibaTemplateDefinition> kuwaibaTemplateList = new ArrayList<KuwaibaTemplateDefinition>();

   private List<KuwaibaClass> kuwaibaClassList = new ArrayList<KuwaibaClass>();

   private List<KuwaibaConnection> kuwaibaConnectionList = new ArrayList<KuwaibaConnection>();

   public KuwaibaProvisioningRequisition() {
      super();
   }

   public List<KuwaibaTemplateDefinition> getKuwaibaTemplateList() {
      return kuwaibaTemplateList;
   }

   public void setKuwaibaTemplateList(List<KuwaibaTemplateDefinition> kuwaibaTemplateList) {
      this.kuwaibaTemplateList = kuwaibaTemplateList;
   }

   public List<KuwaibaClass> getKuwaibaClassList() {
      return kuwaibaClassList;
   }

   public void setKuwaibaClassList(List<KuwaibaClass> kuwaibaClassList) {
      this.kuwaibaClassList = kuwaibaClassList;
   }

   public List<KuwaibaConnection> getKuwaibaConnectionList() {
      return kuwaibaConnectionList;
   }

   public void setKuwaibaConnectionList(List<KuwaibaConnection> kuwaibaConnectionList) {
      this.kuwaibaConnectionList = kuwaibaConnectionList;
   }

   @Override
   public String toString() {
      return "KuwaibaProvisioningRequisition [kuwaibaTemplateList=" + kuwaibaTemplateList + ", kuwaibaClassList=" + kuwaibaClassList +
               ", kuwaibaConnectionList=" + kuwaibaConnectionList + "]";
   }

}

