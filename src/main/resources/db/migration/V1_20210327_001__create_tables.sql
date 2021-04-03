create table if not exists cmddata.ping
(
    id                  integer
  , run_date_time       timestamp      not null
  , packets_transmitted	integer
  , packets_received	integer
  , mintime_millis	    integer
  , avgtime_millis	    integer
  , maxtime_millis      integer
  , constraint ping_cp_pk primary key (id)
);

create sequence cmddata.ping_id_seq increment by 1 minvalue 1 maxvalue 9999999999 cache 1 no cycle;

---------------------------------------------------------------------------------------------------

create table if not exists cmddata.speedtest
(
    id                      integer
  , run_date_time           timestamp      not null
  , latency_millis	    	numeric(10,2)
  , jitter_millis	    	numeric(10,2)
  , downloadspeed_bytes	    integer
  , uploadspeed_bytes	    integer
  ,	packet_loss_perc  		numeric(10,2)
  , all_output              varchar(2048)
  , constraint speedtest_cp_pk primary key (id)
);

create sequence cmddata.speed_id_seq increment by 1 minvalue 1 maxvalue 9999999999 cache 1 no cycle;
