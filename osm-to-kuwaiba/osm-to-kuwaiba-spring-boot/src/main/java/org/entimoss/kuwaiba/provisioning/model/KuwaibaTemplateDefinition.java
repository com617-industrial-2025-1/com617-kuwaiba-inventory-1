package org.entimoss.kuwaiba.provisioning.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KuwaibaTemplateDefinition {

   private String templateName = null;

   private String templateElementName = null;

   private String className = null;

   private String templateFunction = null;

   private Boolean special = false;

   private List<KuwaibaTemplateDefinition> childKuwaibaTemplateDefinitions = new ArrayList<KuwaibaTemplateDefinition>();

   private HashMap<String, String> templateFunctionAttributes = new HashMap<String, String>();

   private HashMap<String, String> templateAttributes = new HashMap<String, String>();

   public KuwaibaTemplateDefinition() {
      super();
   }

   public String getTemplateName() {
      return templateName;
   }

   public void setTemplateName(String templateName) {
      this.templateName = templateName;
   }

   public String getTemplateElementName() {
      return templateElementName;
   }

   public void setTemplateElementName(String templateElementName) {
      this.templateElementName = templateElementName;
   }

   public List<KuwaibaTemplateDefinition> getChildKuwaibaTemplateDefinitions() {
      return childKuwaibaTemplateDefinitions;
   }

   public void setChildKuwaibaTemplateDefinitions(List<KuwaibaTemplateDefinition> childKuwaibaTemplateDefinitions) {
      this.childKuwaibaTemplateDefinitions = childKuwaibaTemplateDefinitions;
   }

   public String getClassName() {
      return className;
   }

   public void setClassName(String className) {
      this.className = className;
   }

   public String getTemplateFunction() {
      return templateFunction;
   }

   public void setTemplateFunction(String templateFunction) {
      this.templateFunction = templateFunction;
   }

   public HashMap<String, String> getTemplateFunctionAttributes() {
      return templateFunctionAttributes;
   }

   public void setTemplateFunctionAttributes(HashMap<String, String> templateFunctionAttributes) {
      this.templateFunctionAttributes = templateFunctionAttributes;
   }

   public Boolean getSpecial() {
      return special;
   }

   public void setSpecial(Boolean special) {
      this.special = special;
   }

   public HashMap<String, String> getTemplateAttributes() {
      return templateAttributes;
   }

   public void setTemplateAttributes(HashMap<String, String> templateAttributes) {
      this.templateAttributes = templateAttributes;
   }

   @Override
   public String toString() {
      return "KuwaibaTemplateDefinition [templateName=" + templateName + ", templateElementName=" + templateElementName +
               ", className=" + className + ", templateFunction=" + templateFunction + ", special=" + special +
               ", childKuwaibaTemplateDefinitions=" + childKuwaibaTemplateDefinitions +
               ", templateFunctionAttributes=" + templateFunctionAttributes + ", templateAttributes=" + templateAttributes + "]";
   }

}