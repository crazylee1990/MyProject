use Q2;

alter database Q2 character set utf8mb4;
set names utf8mb4;

DROP TABLE IF EXISTS tweets_q4;

CREATE TABLE tweets_q4(
  date_loc varchar(100),
  tag text,
  tweet_id mediumtext,
  rank int unsigned
) ENGINE=MyISAM;

LOAD DATA LOCAL INFILE 'q4_data'
INTO TABLE tweets_q4
FIELDS TERMINATED BY '\t'
LINES TERMINATED BY '\n';

CREATE INDEX idx_sign_rank ON tweets_q4 (date_loc, rank);

select * from tweets_q4 limit 100;