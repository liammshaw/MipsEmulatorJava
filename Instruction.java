public class Instruction {
   private String opcode;
   private String rs;
   private String rt;
   private String rd;
   private String shamt;
   private String funct;
   private String immediate;
   private String address;

   public Instruction(){
      opcode = null;
      rs = null;
      rt = null;
      rd = null;
      shamt = null;
      funct = null;
      immediate = null;
      address = null;
   }
   
   public String getAddress() {
      return address;
   }
   public String getFunct() {
      return funct;
   }
   public String getImmediate() {
      return immediate;
   }
   public String getOpcode() {
      return opcode;
   }
   public String getRd() {
      return rd;
   }
   public String getRs() {
      return rs;
   }
   public String getRt() {
      return rt;
   }
   public String getShamt() {
      return shamt;
   }

   public void setAddress(String address) {
      this.address = address;
   }
   public void setFunct(String funct) {
      this.funct = funct;
   }
   public void setImmediate(String immediate) {
      this.immediate = immediate;
   }
   public void setOpcode(String opcode) {
      this.opcode = opcode;
   }
   public void setRd(String rd) {
      this.rd = rd;
   }
   public void setRs(String rs) {
      this.rs = rs;
   }
   public void setRt(String rt) {
      this.rt = rt;
   }
   public void setShamt(String shamt) {
      this.shamt = shamt;
   }
}
