package Messages;

public enum MessageType {
   DIFF("DIFF"), MESS("MESS"), ACKM("ACKM"), 
   LAST("LAST"), OLDM("OLDM"), ENDM("ENDM"), 
   REGI("REGI"), REOK("REOK"), RENO("RENO"), 
   RUOK("RUOK"), IMOK("IMOK"), LIST("LIST"), 
   LINB("LINB"), ITEM("ITEM"), MGES("MGES"), 
   MDIF("MDIF"), MDOK("MDOK"), MERR("MERR"),
   MGOK("MGOK"), NONE("");

   String value;

   private MessageType(String value) {
      this.value = value;
   }

   public String getValue() {
      return value;
   }

}