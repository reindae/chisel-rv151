module mux_dmem_data(
  input         clock,
  input         reset,
  input  [31:0] io_rs2,
  input  [31:0] io_alu_out,
  input  [31:0] io_mem_out,
  input  [1:0]  io_sel,
  output [31:0] io_dmem_data
);
  wire [31:0] _io_dmem_data_T_3 = 2'h1 == io_sel ? io_rs2 : io_alu_out; // @[Mux.scala 81:58]
  assign io_dmem_data = 2'h2 == io_sel ? io_mem_out : _io_dmem_data_T_3; // @[Mux.scala 81:58]
endmodule
