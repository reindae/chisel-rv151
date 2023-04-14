module data_out(
  input         clock,
  input         reset,
  input         io_rst,
  input         io_sign_ext,
  input  [31:0] io_dmem_dout,
  input  [31:0] io_bios_doutb,
  input         io_rx_fifo_empty,
  input         io_tx_fifo_full,
  input  [7:0]  io_rx_fifo_out,
  input  [31:0] io_cycle_p,
  input  [31:0] io_inst_p,
  input  [31:0] io_corr_B_p,
  input  [31:0] io_total_B_p,
  input  [3:0]  io_mem_out,
  input  [31:0] io_prev_data_addr,
  input         io_uart_rx_data_out_valid,
  input         io_uart_tx_data_in_ready,
  output [31:0] io_data_out
);
  wire  _data_T_1 = ~io_rx_fifo_empty; // @[data_out.scala 44:41]
  wire [31:0] _data_T_2 = {30'h0,_data_T_1,io_uart_tx_data_in_ready}; // @[Cat.scala 33:92]
  wire [31:0] _data_T_3 = {24'h0,io_rx_fifo_out}; // @[Cat.scala 33:92]
  wire [31:0] _data_T_5 = 32'h80000000 == io_prev_data_addr ? _data_T_2 : 32'h0; // @[Mux.scala 81:58]
  wire [31:0] _data_T_7 = 32'h80000004 == io_prev_data_addr ? _data_T_3 : _data_T_5; // @[Mux.scala 81:58]
  wire [31:0] _data_T_9 = 32'h80000010 == io_prev_data_addr ? io_cycle_p : _data_T_7; // @[Mux.scala 81:58]
  wire [31:0] _data_T_11 = 32'h80000014 == io_prev_data_addr ? io_inst_p : _data_T_9; // @[Mux.scala 81:58]
  wire [31:0] _data_T_13 = 32'h8000001c == io_prev_data_addr ? io_total_B_p : _data_T_11; // @[Mux.scala 81:58]
  wire [31:0] _data_T_15 = 32'h80000020 == io_prev_data_addr ? io_corr_B_p : _data_T_13; // @[Mux.scala 81:58]
  wire [31:0] _data_T_17 = 4'h1 == io_prev_data_addr[31:28] ? io_dmem_dout : 32'h0; // @[Mux.scala 81:58]
  wire [31:0] _data_T_19 = 4'h3 == io_prev_data_addr[31:28] ? io_dmem_dout : _data_T_17; // @[Mux.scala 81:58]
  wire [31:0] _data_T_21 = 4'h4 == io_prev_data_addr[31:28] ? io_bios_doutb : _data_T_19; // @[Mux.scala 81:58]
  wire [31:0] _data_T_23 = 4'h8 == io_prev_data_addr[31:28] ? _data_T_15 : _data_T_21; // @[Mux.scala 81:58]
  wire [31:0] data = io_rst ? 32'h0 : _data_T_23; // @[data_out.scala 35:17 36:10 39:10]
  wire  _io_data_out_T_1 = io_sign_ext & data[7]; // @[data_out.scala 60:41]
  wire [23:0] _io_data_out_T_3 = _io_data_out_T_1 ? 24'hffffff : 24'h0; // @[Bitwise.scala 77:12]
  wire [31:0] _io_data_out_T_5 = {_io_data_out_T_3,data[7:0]}; // @[Cat.scala 33:92]
  wire  _io_data_out_T_7 = io_sign_ext & data[15]; // @[data_out.scala 61:41]
  wire [23:0] _io_data_out_T_9 = _io_data_out_T_7 ? 24'hffffff : 24'h0; // @[Bitwise.scala 77:12]
  wire [31:0] _io_data_out_T_11 = {_io_data_out_T_9,data[15:8]}; // @[Cat.scala 33:92]
  wire  _io_data_out_T_13 = io_sign_ext & data[23]; // @[data_out.scala 62:41]
  wire [23:0] _io_data_out_T_15 = _io_data_out_T_13 ? 24'hffffff : 24'h0; // @[Bitwise.scala 77:12]
  wire [31:0] _io_data_out_T_17 = {_io_data_out_T_15,data[23:16]}; // @[Cat.scala 33:92]
  wire  _io_data_out_T_19 = io_sign_ext & data[31]; // @[data_out.scala 63:41]
  wire [23:0] _io_data_out_T_21 = _io_data_out_T_19 ? 24'hffffff : 24'h0; // @[Bitwise.scala 77:12]
  wire [31:0] _io_data_out_T_23 = {_io_data_out_T_21,data[31:24]}; // @[Cat.scala 33:92]
  wire [15:0] _io_data_out_T_27 = _io_data_out_T_7 ? 16'hffff : 16'h0; // @[Bitwise.scala 77:12]
  wire [31:0] _io_data_out_T_29 = {_io_data_out_T_27,data[15:0]}; // @[Cat.scala 33:92]
  wire [15:0] _io_data_out_T_33 = _io_data_out_T_19 ? 16'hffff : 16'h0; // @[Bitwise.scala 77:12]
  wire [31:0] _io_data_out_T_35 = {_io_data_out_T_33,data[31:16]}; // @[Cat.scala 33:92]
  wire [31:0] _io_data_out_T_37 = 4'h1 == io_mem_out ? _io_data_out_T_5 : 32'h0; // @[Mux.scala 81:58]
  wire [31:0] _io_data_out_T_39 = 4'h2 == io_mem_out ? _io_data_out_T_11 : _io_data_out_T_37; // @[Mux.scala 81:58]
  wire [31:0] _io_data_out_T_41 = 4'h4 == io_mem_out ? _io_data_out_T_17 : _io_data_out_T_39; // @[Mux.scala 81:58]
  wire [31:0] _io_data_out_T_43 = 4'h8 == io_mem_out ? _io_data_out_T_23 : _io_data_out_T_41; // @[Mux.scala 81:58]
  wire [31:0] _io_data_out_T_45 = 4'h3 == io_mem_out ? _io_data_out_T_29 : _io_data_out_T_43; // @[Mux.scala 81:58]
  wire [31:0] _io_data_out_T_47 = 4'hc == io_mem_out ? _io_data_out_T_35 : _io_data_out_T_45; // @[Mux.scala 81:58]
  wire [31:0] _io_data_out_T_49 = 4'hf == io_mem_out ? data : _io_data_out_T_47; // @[Mux.scala 81:58]
  assign io_data_out = io_rst ? 32'h0 : _io_data_out_T_49; // @[data_out.scala 55:17 56:17 59:17]
endmodule
