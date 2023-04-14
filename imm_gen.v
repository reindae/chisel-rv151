module imm_gen(
  input         clock,
  input         reset,
  input  [31:0] io_inst,
  output [31:0] io_imm
);
  wire [31:0] _io_imm_T_1 = {io_inst[31:12],12'h0}; // @[Cat.scala 33:92]
  wire [11:0] _io_imm_T_4 = io_inst[31] ? 12'hfff : 12'h0; // @[Bitwise.scala 77:12]
  wire [31:0] _io_imm_T_8 = {_io_imm_T_4,io_inst[19:12],io_inst[20],io_inst[30:21],1'h0}; // @[Cat.scala 33:92]
  wire [19:0] _io_imm_T_11 = io_inst[31] ? 20'hfffff : 20'h0; // @[Bitwise.scala 77:12]
  wire [31:0] _io_imm_T_13 = {_io_imm_T_11,io_inst[31:20]}; // @[Cat.scala 33:92]
  wire [31:0] _io_imm_T_20 = {_io_imm_T_11,io_inst[7],io_inst[30:25],io_inst[11:8],1'h0}; // @[Cat.scala 33:92]
  wire [31:0] _io_imm_T_26 = {_io_imm_T_11,io_inst[31:25],io_inst[11:7]}; // @[Cat.scala 33:92]
  wire [31:0] _io_imm_T_28 = {27'h0,io_inst[19:15]}; // @[Cat.scala 33:92]
  wire [31:0] _GEN_0 = 7'h73 == io_inst[6:0] ? _io_imm_T_28 : 32'h0; // @[imm_gen.scala 36:10 38:25 55:14]
  wire [31:0] _GEN_1 = 7'h23 == io_inst[6:0] ? _io_imm_T_26 : _GEN_0; // @[imm_gen.scala 38:25 52:14]
  wire [31:0] _GEN_2 = 7'h63 == io_inst[6:0] ? _io_imm_T_20 : _GEN_1; // @[imm_gen.scala 38:25 49:14]
  wire [31:0] _GEN_3 = 7'h67 == io_inst[6:0] | 7'h3 == io_inst[6:0] | 7'h13 == io_inst[6:0] ? _io_imm_T_13 : _GEN_2; // @[imm_gen.scala 38:25 46:14]
  wire [31:0] _GEN_4 = 7'h6f == io_inst[6:0] ? _io_imm_T_8 : _GEN_3; // @[imm_gen.scala 38:25 43:14]
  assign io_imm = 7'h37 == io_inst[6:0] | 7'h17 == io_inst[6:0] ? _io_imm_T_1 : _GEN_4; // @[imm_gen.scala 38:25 40:14]
endmodule
