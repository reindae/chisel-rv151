module branch_comp(
  input         clock,
  input         reset,
  input  [31:0] io_rs1,
  input  [31:0] io_rs2,
  input         io_BrUn,
  output        io_BrEq,
  output        io_BrLt
);
  assign io_BrEq = io_BrUn ? io_rs1 == io_rs2 : $signed(io_rs1) == $signed(io_rs2); // @[branch_comp.scala 22:29 24:13 28:13]
  assign io_BrLt = io_BrUn ? io_rs1 < io_rs2 : $signed(io_rs1) < $signed(io_rs2); // @[branch_comp.scala 22:29 23:13 27:13]
endmodule
