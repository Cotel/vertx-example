create table player
(
	id varchar not null
		constraint player_pkey
			primary key,
	name varchar(50) not null
)
;

alter table player owner to postgres
;
