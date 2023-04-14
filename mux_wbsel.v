module mux_wbsel(
  input         clock,
  input         reset,
  input  [31:0] io_pc,
  input  [31:0] io_alu_out,
  input  [31:0] io_data_out,
  input  [1:0]  io_wb_sel,
  output [31:0] io_wb_out
);
  wire [31:0] _io_wb_out_T_1 = io_pc + 32'h4; // @[mux_wbsel.scala 22:44]
  wire [31:0] _io_wb_out_T_5 = 2'h0 == io_wb_sel ? _io_wb_out_T_1 : _io_wb_out_T_1; // @[Mux.scala 81:58]
  wire [31:0] _io_wb_out_T_7 = 2'h1 == io_wb_sel ? io_alu_out : _io_wb_out_T_5; // @[Mux.scala 81:58]
  assign io_wb_out = 2'h2 == io_wb_sel ? io_data_out : _io_wb_out_T_7; // @[Mux.scala 81:58]
endmodule
