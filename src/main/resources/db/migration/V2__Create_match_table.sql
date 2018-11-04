create table match
(
	id varchar not null
		constraint match_pkey
			primary key,
	starting_date bigint not null,
	ending_date bigint default 0
)
;

alter table match owner to postgres
;

create table match_player
(
	match_id varchar
		constraint match_player_match_id_fk
			references match,
	player_id varchar
		constraint match_player_player_id_fk
			references player,
	constraint match_player_pkey primary key (match_id, player_id)
)
;

alter table match_player owner to postgres
;

