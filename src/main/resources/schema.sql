-- PLAY26 Database Schema
-- Sports Turf Booking & Cricket Tournament Management

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS turfs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(255),
    description TEXT,
    price_per_hour DECIMAL(10,2),
    supported_sports VARCHAR(255),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_turfs_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS turf_slots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    turf_id BIGINT NOT NULL,
    slot_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (turf_id) REFERENCES turfs(id) ON DELETE CASCADE,
    INDEX idx_slots_turf (turf_id),
    INDEX idx_slots_date (slot_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    turf_slot_id BIGINT NOT NULL,
    booking_status VARCHAR(20) DEFAULT 'CONFIRMED',
    total_price DECIMAL(10,2) DEFAULT 0,
    amount_paid DECIMAL(10,2) DEFAULT 0,
    split_link_uuid VARCHAR(100) UNIQUE,
    payment_type VARCHAR(20) DEFAULT 'FULL',
    booked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (turf_slot_id) REFERENCES turf_slots(id),
    INDEX idx_bookings_user (user_id),
    INDEX idx_bookings_split (split_link_uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS split_contributions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    contributor_name VARCHAR(100) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    INDEX idx_contributions_booking (booking_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sports_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sport_name VARCHAR(100),
    title VARCHAR(200),
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tournaments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_name VARCHAR(200) NOT NULL,
    description TEXT,
    tournament_type VARCHAR(30),
    match_type VARCHAR(20),
    overs INT,
    ball_type VARCHAR(20),
    entry_fee DECIMAL(10,2),
    prize_pool DECIMAL(12,2),
    maximum_teams INT DEFAULT 8,
    registered_teams INT DEFAULT 0,
    venue VARCHAR(255),
    start_date DATE,
    end_date DATE,
    banner VARCHAR(500),
    rules TEXT,
    status VARCHAR(20) DEFAULT 'UPCOMING',
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_tournaments_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS teams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT,
    team_name VARCHAR(100) NOT NULL,
    short_name VARCHAR(10),
    team_code VARCHAR(10),
    logo VARCHAR(500),
    city VARCHAR(100),
    description TEXT,
    coach_name VARCHAR(100),
    captain_id BIGINT,
    vice_captain_id BIGINT,
    home_ground VARCHAR(200),
    matches_played INT DEFAULT 0,
    wins INT DEFAULT 0,
    losses INT DEFAULT 0,
    ties INT DEFAULT 0,
    no_result INT DEFAULT 0,
    points INT DEFAULT 0,
    net_run_rate DECIMAL(6,3) DEFAULT 0.000,
    total_runs INT DEFAULT 0,
    total_wickets INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
    INDEX idx_teams_tournament (tournament_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT,
    team_id BIGINT,
    player_name VARCHAR(100) NOT NULL,
    jersey_number INT,
    photo VARCHAR(500),
    role VARCHAR(30),
    batting_style VARCHAR(30),
    bowling_style VARCHAR(50),
    wicket_keeper BOOLEAN DEFAULT FALSE,
    captain BOOLEAN DEFAULT FALSE,
    vice_captain BOOLEAN DEFAULT FALSE,
    date_of_birth DATE,
    age INT,
    nationality VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(160),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
    FOREIGN KEY (team_id) REFERENCES teams(id),
    INDEX idx_players_team (team_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS matches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT,
    match_number VARCHAR(20),
    team_a_id BIGINT,
    team_b_id BIGINT,
    turf_id BIGINT,
    match_date DATE,
    match_time TIME,
    venue VARCHAR(255),
    overs INT,
    toss_winner BIGINT,
    toss_decision VARCHAR(10),
    winner BIGINT,
    man_of_match BIGINT,
    match_stage VARCHAR(30),
    result TEXT,
    status VARCHAR(20) DEFAULT 'UPCOMING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
    FOREIGN KEY (team_a_id) REFERENCES teams(id),
    FOREIGN KEY (team_b_id) REFERENCES teams(id),
    FOREIGN KEY (turf_id) REFERENCES turfs(id),
    FOREIGN KEY (toss_winner) REFERENCES teams(id),
    FOREIGN KEY (winner) REFERENCES teams(id),
    FOREIGN KEY (man_of_match) REFERENCES players(id),
    INDEX idx_matches_tournament (tournament_id),
    INDEX idx_matches_status (status),
    INDEX idx_matches_date (match_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS scorecards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_id BIGINT NOT NULL,
    innings INT NOT NULL,
    batting_team_id BIGINT NOT NULL,
    bowling_team_id BIGINT NOT NULL,
    total_runs INT DEFAULT 0,
    total_wickets INT DEFAULT 0,
    total_overs DECIMAL(4,1) DEFAULT 0.0,
    extras INT DEFAULT 0,
    target INT,
    status VARCHAR(20) DEFAULT 'IN_PROGRESS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (match_id) REFERENCES matches(id) ON DELETE CASCADE,
    FOREIGN KEY (batting_team_id) REFERENCES teams(id),
    FOREIGN KEY (bowling_team_id) REFERENCES teams(id),
    INDEX idx_scorecards_match (match_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS batting_scores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scorecard_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    runs INT DEFAULT 0,
    balls INT DEFAULT 0,
    fours INT DEFAULT 0,
    sixes INT DEFAULT 0,
    strike_rate DECIMAL(6,2) DEFAULT 0.00,
    is_out BOOLEAN DEFAULT FALSE,
    dismissal_type VARCHAR(30),
    bowler_id BIGINT,
    fielder_id BIGINT,
    batting_position INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (scorecard_id) REFERENCES scorecards(id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players(id),
    FOREIGN KEY (bowler_id) REFERENCES players(id),
    FOREIGN KEY (fielder_id) REFERENCES players(id),
    INDEX idx_batting_scorecard (scorecard_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS bowling_scores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scorecard_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    overs DECIMAL(4,1) DEFAULT 0.0,
    maidens INT DEFAULT 0,
    runs INT DEFAULT 0,
    wickets INT DEFAULT 0,
    economy DECIMAL(5,2) DEFAULT 0.00,
    dots INT DEFAULT 0,
    wides INT DEFAULT 0,
    no_balls INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (scorecard_id) REFERENCES scorecards(id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players(id),
    INDEX idx_bowling_scorecard (scorecard_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS extras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scorecard_id BIGINT NOT NULL,
    wides INT DEFAULT 0,
    no_balls INT DEFAULT 0,
    byes INT DEFAULT 0,
    leg_byes INT DEFAULT 0,
    penalty INT DEFAULT 0,
    total INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (scorecard_id) REFERENCES scorecards(id) ON DELETE CASCADE,
    UNIQUE INDEX idx_extras_scorecard (scorecard_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    turf_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (turf_id) REFERENCES turfs(id) ON DELETE CASCADE,
    INDEX idx_reviews_turf (turf_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
