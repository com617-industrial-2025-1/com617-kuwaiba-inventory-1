package org.entimoss.kuwaiba.provisioning.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KuwaibaClass {

   private String className = null;
   private String templateName = null;
   private String name = null;
   private Boolean special = false;

   // parent classes contains a hierarchy of classes to be searched in order to find parent.
   private List<KuwaibaClass> parentClasses = new ArrayList<KuwaibaClass>();

   private HashMap<String, String> attributes = new HashMap<String, String>();

   public KuwaibaClass() {
      super();
   }

   public String getClassName() {
      return className;
   }

   public void setClassName(String className) {
      this.className = className;
   }

   public String getTemplateName() {
      return templateName;
   }

   public void setTemplateName(String templateName) {
      this.templateName = templateName;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<KuwaibaClass> getParentClasses() {
      return parentClasses;
   }

   public void setParentClasses(List<KuwaibaClass> parentClasses) {
      this.parentClasses = parentClasses;
   }

   public HashMap<String, String> getAttributes() {
      return attributes;
   }

   public void setAttributes(HashMap<String, String> attributes) {
      this.attributes = attributes;
   }

   public Boolean getSpecial() {
      return special;
   }

   public void setSpecial(Boolean special) {
      this.special = special;
   }

   @Override
   public String toString() {
      return "KuwaibaClass [ name=" + name + ", className=" + className + ", templateName=" + templateName + ", special=" + special +
               ", parentClasses=" + parentClasses + ", attributes=" + attributes + "]";
   }

}

