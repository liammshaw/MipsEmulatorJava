import java.io.*;
import java.util.*;

public class lab3 {
   public static int[] regMem = new int[27];
   public static int[] dataMemory = new int[8192];
   public static void main(String[] args) {
      try{
         HashMap<String, Integer> labels = new HashMap<String, Integer>();
         HashMap<Integer, String> registerNames = getRegisterName();
         ArrayList<Instruction> instructions = new ArrayList<Instruction>();
         Scanner inFile = new Scanner(new File(args[0]));
         int lineCounter = 0;
         int i;
         while(inFile.hasNextLine()){
            String currentLine = inFile.nextLine();
            if((i = currentLine.indexOf(":")) != -1){
               labels.put(currentLine.substring(0,i), lineCounter);
            }
            if(!currentLine.trim().isEmpty() && (currentLine.trim().indexOf("#")) != 0){
               lineCounter++;
            }
         }
         inFile.close();


         inFile = new Scanner(new File(args[0]));
         lineCounter = 0;
         while(inFile.hasNextLine()){
            String currentLine = inFile.nextLine();
            currentLine = format(currentLine);
            if(currentLine.trim().isEmpty()){
               continue;
            }
            Instruction instruction = createInstruction(currentLine.split("\\s+"), labels, lineCounter);
            
            instructions.add(instruction);

            if(!currentLine.trim().isEmpty() && (currentLine.trim().indexOf("#")) != 0){
               lineCounter++;
            }
         }
         inFile.close();

         // for(Instruction inst: instructions){
         //    System.out.println(inst.getOpcode());
         // }

         if(args.length > 1)
            runWithScript(instructions, registerNames, new File(args[1]));
         else
            runWithSTDIN(instructions, registerNames);
      }
      catch(FileNotFoundException e){
         e.printStackTrace();
      }
   }

   public static void runWithScript(ArrayList<Instruction> instructions, HashMap<Integer, String> registerNames, File script) throws FileNotFoundException{
      Scanner userIn = new Scanner(script);
      int pc = 0;

      System.out.print("mips> ");
      String command = userIn.nextLine();
      System.out.println(command);
      while(!command.equals("q") && userIn.hasNextLine()){
         pc = runCommand(command, instructions, pc, registerNames);
         System.out.print("mips> ");
         command = userIn.nextLine();
         System.out.println(command);
      }

      userIn.close();
   }

   public static void runWithSTDIN(ArrayList<Instruction> instructions, HashMap<Integer, String> registerNames){
      Scanner userIn = new Scanner(System.in);
      int pc = 0;

      System.out.print("mips> ");
      String command = userIn.nextLine();
      while(!command.equals("q")){
         pc = runCommand(command, instructions, pc, registerNames);
         System.out.print("mips> ");
         command = userIn.nextLine();
      }

      userIn.close();
   }

   public static int runCommand(String command, 
      List<Instruction> instructions, 
      int pc, 
      HashMap<Integer, String> registerNames){
      String cmd = command;
      if(cmd.length() > 1){
         cmd = command.substring(0,1);
      }
      if(cmd.equals("h")){
         System.out.println("\nh = show help");
         System.out.println("d = dump register state");
         System.out.println("s = single step through the program (i.e. execute 1 instruction and stop)");
         System.out.println("s num = step through num instructions of the program");
         System.out.println("r = run until the program ends");
         System.out.println("m num1 num2 = display data memory from location num1 to num2");
         System.out.println("c = clear all registers, memory, and the program counter to 0");
         System.out.println("q = exit the program\n");
      }
      if(cmd.equals("d")){
         System.out.println("\npc = " + pc);
         int counter = 0;
         for(int i = 0; i < regMem.length; i++){
            if(i == 0)
               System.out.printf("%-2s = %d          ", registerNames.get(i), regMem[i]);
            else
               System.out.printf("%-3s = %d         ", registerNames.get(i), regMem[i]);
            if(counter % 4 == 3){
               System.out.print("\n");
            }
            counter++;
         }
         System.out.println("\n");
      }
      if(cmd.equals("s")){
         String[] args = command.split("\\s+");
         if(args.length > 1){
            int nums = Integer.parseInt(args[1]);
            for(int i = 0; i < nums; i++){
               pc = executeInstruction(instructions.get(pc), registerNames, pc);
            }
            System.out.printf("        %d instruction(s) executed\n", nums);
         }
         else {
            pc = executeInstruction(instructions.get(pc), registerNames, pc);
            System.out.println("        1 instruction(s) executed");
         }
      }
      if(cmd.equals("r")){
         while(pc < instructions.size()){
            pc = executeInstruction(instructions.get(pc), registerNames, pc);
         }
      }
      if(cmd.equals("m")){
         String[] args = command.split("\\s+");
         int num1 = Integer.parseInt(args[1]);
         int num2 = Integer.parseInt(args[2]);
         for(int i = num1; i <= num2; i++){
            System.out.printf("\n[%d] = %d", i, dataMemory[i]);
         }
         System.out.println("\n");
      }
      if(cmd.equals("c")){
         regMem = new int[27];
         pc = 0;
         System.out.println("        Simulator reset\n");
      }
      return pc;
   }

   public static int executeInstruction(Instruction instruction, 
   HashMap<Integer, String> registerNames, 
   int pc) {
      HashMap<String, Integer> registerIndex = getRegisterIndex();
      String type = instruction.getOpcode();
      if(type.equals("and")){
         int Rs = regMem[registerIndex.get(instruction.getRs())];
         int Rt = regMem[registerIndex.get(instruction.getRt())];
         int Rd = registerIndex.get(instruction.getRd());
         int result = Rs & Rt;
         regMem[Rd] = result;
         pc += 1;
      }
      else if(type.equals("or")){
         int Rs = regMem[registerIndex.get(instruction.getRs())];
         int Rt = regMem[registerIndex.get(instruction.getRt())];
         int Rd = registerIndex.get(instruction.getRd());
         int result = Rs | Rt;
         regMem[Rd] = result;
         pc += 1;
      }
      else if(type.equals("add")){
         int Rs = regMem[registerIndex.get(instruction.getRs())];
         int Rt = regMem[registerIndex.get(instruction.getRt())];
         int Rd = registerIndex.get(instruction.getRd());
         int result = Rs + Rt;
         regMem[Rd] = result;
         pc += 1;
      }
      else if(type.equals("addi")){
         int Rs = regMem[registerIndex.get(instruction.getRs())];
         int imm = Integer.parseInt(instruction.getImmediate());
         int Rt = registerIndex.get(instruction.getRt());
         int result = Rs + imm;
         regMem[Rt] = result;
         pc += 1;
      }
      else if(type.equals("sll")){
         int Rt = regMem[registerIndex.get(instruction.getRt())];
         int shamt = Integer.parseInt(instruction.getShamt());
         int Rd = registerIndex.get(instruction.getRt());
         int result = Rt << shamt;
         regMem[Rd] = result;
         pc += 1;
      }
      else if(type.equals("sub")){
         int Rs = regMem[registerIndex.get(instruction.getRs())];
         int Rt = regMem[registerIndex.get(instruction.getRt())];
         int Rd = registerIndex.get(instruction.getRd());
         int result = Rs - Rt;
         regMem[Rd] = result;
         pc += 1;
      }
      else if(type.equals("slt")){
         int Rs = regMem[registerIndex.get(instruction.getRs())];
         int Rt = regMem[registerIndex.get(instruction.getRt())];
         int Rd = registerIndex.get(instruction.getRd());
         int result = 0;
         if(Rs < Rt){
            result = 1;
         }
         regMem[Rd] = result;
         pc += 1;
      }
      else if(type.equals("beq")){
         int Rs = regMem[registerIndex.get(instruction.getRs())];
         int Rt = regMem[registerIndex.get(instruction.getRt())];
         int imm = Integer.parseInt(instruction.getImmediate());
         if(Rs == Rt)
            pc += imm;
         pc+=1;
      }
      else if(type.equals("bne")){
         int Rs = regMem[registerIndex.get(instruction.getRs())];
         int Rt = regMem[registerIndex.get(instruction.getRt())];
         int imm = Integer.parseInt(instruction.getImmediate());
         if(Rs != Rt)
            pc += imm;
         pc+=1;
      }
      else if(type.equals("lw")){
         int Rs = regMem[registerIndex.get(instruction.getRs())];
         int Rt = registerIndex.get(instruction.getRt());
         int imm = Integer.parseInt(instruction.getImmediate());
         int address = Rs + imm;
         regMem[Rt] = dataMemory[address];
         pc += 1;
      }
      else if(type.equals("sw")){
         int Rs = regMem[registerIndex.get(instruction.getRs())];
         int Rt = registerIndex.get(instruction.getRt());
         int imm = Integer.parseInt(instruction.getImmediate());
         int address = Rs + imm;
         dataMemory[address] = regMem[Rt];
         pc += 1;
      }
      else if(type.equals("j")){
         pc = Integer.parseInt(instruction.getAddress());
      }
      else if(type.equals("jr")){
         pc = regMem[registerIndex.get(instruction.getRs())];
      }
      else if(type.equals("jal")){
         regMem[registerIndex.get("$ra")] = pc + 1;
         pc = Integer.parseInt(instruction.getAddress());
      }
      return pc;
   }

   public static Instruction createInstruction(String[] instruction, 
      HashMap<String, Integer> labels, 
      int lineCounter)
      {
      Instruction inst = new Instruction();
      String type = instruction[0];
      if(type.equals("and")){
         inst.setOpcode(type);
         inst.setRd(instruction[1]);
         inst.setRs(instruction[2]);
         inst.setRt(instruction[3]);
      }
      else if(type.equals("or")){
         inst.setOpcode(type);
         inst.setRd(instruction[1]);
         inst.setRs(instruction[2]);
         inst.setRt(instruction[3]);
      }
      else if(type.equals("add")){
         inst.setOpcode(type);
         inst.setRd(instruction[1]);
         inst.setRs(instruction[2]);
         inst.setRt(instruction[3]);
      }
      else if(type.equals("addi")){
         inst.setOpcode(type);
         inst.setRt(instruction[1]);
         inst.setRs(instruction[2]);
         inst.setImmediate(instruction[3]);
      }
      else if(type.equals("sll")){
         inst.setOpcode(type);
         inst.setRd(instruction[1]);
         inst.setRt(instruction[2]);
         inst.setShamt(instruction[3]);
      }
      else if(type.equals("sub")){
         inst.setOpcode(type);
         inst.setRd(instruction[1]);
         inst.setRs(instruction[2]);
         inst.setRt(instruction[3]);
      }
      else if(type.equals("slt")){
         inst.setOpcode(type);
         inst.setRd(instruction[1]);
         inst.setRs(instruction[2]);
         inst.setRt(instruction[3]);
      }
      else if(type.equals("beq")){
         inst.setOpcode(type);
         inst.setRs(instruction[1]);
         inst.setRt(instruction[2]);
         inst.setImmediate(String.valueOf(labels.get(instruction[3]) - (1 + lineCounter)));
      }
      else if(type.equals("bne")){
         inst.setOpcode(type);
         inst.setRs(instruction[1]);
         inst.setRt(instruction[2]);
         inst.setImmediate(String.valueOf(labels.get(instruction[3]) - (1 + lineCounter)));
      }
      else if(type.equals("lw")){
         inst.setOpcode(type);
         inst.setRt(instruction[1]);
         inst.setRs(instruction[2].substring(2, instruction[2].length()-1));
         inst.setImmediate(instruction[2].substring(0,1));
      }
      else if(type.equals("sw")){
         inst.setOpcode(type);
         inst.setRt(instruction[1]);
         inst.setRs(instruction[2].substring(2, instruction[2].length()-1));
         inst.setImmediate(instruction[2].substring(0,1));
      }
      else if(type.equals("j")){
         inst.setOpcode(type);
         inst.setAddress(String.valueOf(labels.get(instruction[1])));
      }
      else if(type.equals("jr")){
         inst.setOpcode(type);
         inst.setRs(instruction[1]);
      }
      else if(type.equals("jal")){
         inst.setOpcode(type);
         inst.setAddress(String.valueOf(labels.get(instruction[1])));
      }
      return inst;
   }

   public static String format(String currentLine){
      int i;
      if((i = currentLine.indexOf(":")) != -1){
         currentLine = currentLine.substring(i+1).trim();
      }
      if((i = currentLine.indexOf("#")) != -1){
         currentLine = currentLine.substring(0,i);
      }
      if((i = currentLine.indexOf("$")) != -1){
         currentLine = currentLine.substring(0, i) + " " + currentLine.substring(i);
      }
      if(currentLine.trim().isEmpty()){
         return "";
      }
      
      currentLine = currentLine.replaceAll(",", " ");
      currentLine = currentLine.trim();
      return currentLine;
   }

   public static HashMap<Integer, String> getRegisterName(){
      HashMap<Integer, String> registers = new HashMap<Integer, String>();
      registers.put(0, "$0");
      // registers.put("$zero", 0);
      registers.put(1, "$v0");
      registers.put(2, "$v1");
      registers.put(3, "$a0");
      registers.put(4, "$a1");
      registers.put(5, "$a2");
      registers.put(6, "$a3");
      registers.put(7, "$t0");
      registers.put(8, "$t1");
      registers.put(9, "$t2");
      registers.put(10, "$t3");
      registers.put(11, "$t4");
      registers.put(12, "$t5");
      registers.put(13, "$t6");
      registers.put(14, "$t7");
      registers.put(23, "$t8");
      registers.put(24, "$t9");
      registers.put(15, "$s0");
      registers.put(16, "$s1");
      registers.put(17, "$s2");
      registers.put(18, "$s3");
      registers.put(19, "$s4");
      registers.put(20, "$s5");
      registers.put(21, "$s6");
      registers.put(22, "$s7");
      registers.put(25, "$sp");
      registers.put(26, "$ra");
      return registers;
   }

   public static HashMap<String, Integer> getRegisterIndex(){
      HashMap<String, Integer> registers = new HashMap<String, Integer>();
      registers.put("$0", 0);
      registers.put("$zero", 0);
      registers.put("$v0", 1);
      registers.put("$v1", 2);
      registers.put("$a0", 3);
      registers.put("$a1", 4);
      registers.put("$a2", 5);
      registers.put("$a3", 6);
      registers.put("$t0", 7);
      registers.put("$t1", 8);
      registers.put("$t2", 9);
      registers.put("$t3", 10);
      registers.put("$t4", 11);
      registers.put("$t5", 12);
      registers.put("$t6", 13);
      registers.put("$t7", 14);
      registers.put("$t8", 23);
      registers.put("$t9", 24);
      registers.put("$s0", 15);
      registers.put("$s1", 16);
      registers.put("$s2", 17);
      registers.put("$s3", 18);
      registers.put("$s4", 19);
      registers.put("$s5", 20);
      registers.put("$s6", 21);
      registers.put("$s7", 22);
      registers.put("$sp", 25);
      registers.put("$ra", 26);
      return registers;
   }
}