CREATE TABLE room (
  room_id      BIGINT AUTO_INCREMENT PRIMARY KEY
    COMMENT '강의실/공간 PK',

  building_id  BIGINT NOT NULL
    COMMENT '소속 건물 ID',

  floor_no     INT NOT NULL
    COMMENT '층',

  room_no      VARCHAR(10) NOT NULL
    COMMENT '호수',

  room_code    VARCHAR(20) NOT NULL
    COMMENT '강의실 코드 (G-05-401)',

  room_type    ENUM('LECTURE','LAB') NOT NULL
    COMMENT '공간 유형',

  capacity     INT NOT NULL
    COMMENT '정원',

  room_name    VARCHAR(100) NOT NULL
    COMMENT '표시 이름',

  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_room_building
    FOREIGN KEY (building_id)
    REFERENCES building(building_id)
    ON DELETE CASCADE,

  CONSTRAINT uq_room_code UNIQUE (room_code),
  CONSTRAINT uq_room_in_building UNIQUE (building_id, floor_no, room_no)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COMMENT='강의실/실습실';

DROP TABLE IF EXISTS seq_1_60;

CREATE TABLE seq_1_60 (
  n INT PRIMARY KEY
);

INSERT INTO seq_1_60 (n) VALUES
(1),(2),(3),(4),(5),(6),(7),(8),(9),(10),
(11),(12),(13),(14),(15),(16),(17),(18),(19),(20),
(21),(22),(23),(24),(25),(26),(27),(28),(29),(30),
(31),(32),(33),(34),(35),(36),(37),(38),(39),(40),
(41),(42),(43),(44),(45),(46),(47),(48),(49),(50),
(51),(52),(53),(54),(55),(56),(57),(58),(59),(60);

INSERT INTO room
(building_id, floor_no, room_no, room_code, room_type, capacity, room_name)
SELECT
  b.building_id,
  1 + MOD(s.n - 1, b.floors)                                   AS floor_no,
  LPAD(1 + FLOOR((s.n - 1) / b.floors), 2, '0')               AS room_no,
  CONCAT(
    b.building_code, '-',
    1 + MOD(s.n - 1, b.floors),
    LPAD(1 + FLOOR((s.n - 1) / b.floors), 2, '0')
  )                                                           AS room_code,
  'LECTURE',
  50,
  CONCAT('강의실 ',
    1 + MOD(s.n - 1, b.floors),
    LPAD(1 + FLOOR((s.n - 1) / b.floors), 2, '0')
  )
FROM building b
JOIN seq_1_60 s ON s.n <= b.lecture_rooms;

INSERT INTO room
(building_id, floor_no, room_no, room_code, room_type, capacity, room_name)
SELECT
  b.building_id,
  1 + MOD(s.n - 1, b.floors),
  LPAD(b.lecture_rooms + 1 + FLOOR((s.n - 1) / b.floors), 2, '0'),
  CONCAT(
    b.building_code, '-',
    1 + MOD(s.n - 1, b.floors),
    LPAD(b.lecture_rooms + 1 + FLOOR((s.n - 1) / b.floors), 2, '0')
  ),
  'LAB',
  30,
  CONCAT('실습실 ',
    1 + MOD(s.n - 1, b.floors),
    LPAD(b.lecture_rooms + 1 + FLOOR((s.n - 1) / b.floors), 2, '0')
  )
FROM building b
JOIN seq_1_60 s ON s.n <= b.lab_rooms;
