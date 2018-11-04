CREATE TABLE public.round
(
    id varchar PRIMARY KEY,
    match_id varchar NOT NULL,
    left_player_id varchar NOT NULL,
    right_player_id varchar,
    left_score int NOT NULL,
    right_score int NOT NULL,
    is_draw boolean DEFAULT false  NOT NULL,
    is_left_bollo boolean DEFAULT false  NOT NULL,
    is_right_bollo boolean DEFAULT false  NOT NULL,
    CONSTRAINT round_match_id_fk FOREIGN KEY (match_id) REFERENCES public.match (id),
    CONSTRAINT round_player_id_fk FOREIGN KEY (left_player_id) REFERENCES public.player (id),
    CONSTRAINT round_player_id_fk_2 FOREIGN KEY (right_player_id) REFERENCES public.player (id)
);
