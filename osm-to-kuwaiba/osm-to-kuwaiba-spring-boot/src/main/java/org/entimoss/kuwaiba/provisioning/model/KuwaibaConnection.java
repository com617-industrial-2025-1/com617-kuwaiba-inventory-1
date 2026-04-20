package org.entimoss.kuwaiba.provisioning.model;


public class KuwaibaConnection {

   private KuwaibaClass connectionClass;

   private KuwaibaClass endpointA;

   private KuwaibaClass endpointB;

   public KuwaibaConnection() {
      super();
   }

   public KuwaibaClass getConnectionClass() {
      return connectionClass;
   }

   public void setConnectionClass(KuwaibaClass connectionClass) {
      this.connectionClass = connectionClass;
   }

   public KuwaibaClass getEndpointA() {
      return endpointA;
   }

   public void setEndpointA(KuwaibaClass endpointA) {
      this.endpointA = endpointA;
   }

   public KuwaibaClass getEndpointB() {
      return endpointB;
   }

   public void setEndpointB(KuwaibaClass endpointB) {
      this.endpointB = endpointB;
   }

   @Override
   public String toString() {
      return "KuwaibaConnection [connectionClass=" + connectionClass + ", endpointA=" + endpointA + ", endpointB=" + endpointB + "]";
   }

}