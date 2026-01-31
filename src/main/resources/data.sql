use solta;

INSERT INTO tag (id, tag_key, kor_name)
VALUES (1, 'math', '수학'),
       (2, 'implementation', '구현'),
       (3, 'dp', '다이나믹 프로그래밍'),
       (4, 'graphs', '그래프 이론'),
       (5, 'data_structures', '자료 구조'),
       (6, 'greedy', '그리디 알고리즘'),
       (7, 'string', '문자열'),
       (8, 'bruteforcing', '브루트포스 알고리즘'),
       (9, 'graph_traversal', '그래프 탐색'),
       (10, 'sorting', '정렬'),
       (11, 'ad_hoc', '애드 혹'),
       (12, 'geometry', '기하학'),
       (13, 'trees', '트리'),
       (14, 'number_theory', '정수론'),
       (15, 'segtree', '세그먼트 트리'),
       (16, 'binary_search', '이분 탐색'),
       (17, 'set', '집합과 맵'),
       (18, 'constructive', '해 구성하기'),
       (19, 'simulation', '시뮬레이션'),
       (20, 'arithmetic', '사칙연산'),
       (21, 'prefix_sum', '누적 합'),
       (22, 'combinatorics', '조합론'),
       (23, 'bfs', '너비 우선 탐색'),
       (24, 'case_work', '많은 조건 분기'),
       (25, 'dfs', '깊이 우선 탐색'),
       (26, 'shortest_path', '최단 경로'),
       (27, 'bitmask', '비트마스킹'),
       (28, 'hash_set', '해시를 사용한 집합과 맵'),
       (29, 'dijkstra', '데이크스트라'),
       (30, 'backtracking', '백트래킹'),
       (31, 'sweeping', '스위핑'),
       (32, 'disjoint_set', '분리 집합'),
       (33, 'dp_tree', '트리에서의 다이나믹 프로그래밍'),
       (34, 'tree_set', '트리를 사용한 집합과 맵'),
       (35, 'parsing', '파싱'),
       (36, 'priority_queue', '우선순위 큐'),
       (37, 'divide_and_conquer', '분할 정복'),
       (38, 'parametric_search', '매개 변수 탐색'),
       (39, 'two_pointer', '두 포인터'),
       (40, 'game_theory', '게임 이론'),
       (41, 'stack', '스택'),
       (42, 'probability', '확률론'),
       (43, 'primality_test', '소수 판정'),
       (44, 'flow', '최대 유량'),
       (45, 'lazyprop', '느리게 갱신되는 세그먼트 트리'),
       (46, 'dp_bitfield', '비트필드를 이용한 다이나믹 프로그래밍'),
       (47, 'exponentiation_by_squaring', '분할 정복을 이용한 거듭제곱'),
       (48, 'offline_queries', '오프라인 쿼리'),
       (49, 'knapsack', '배낭 문제'),
       (50, 'recursion', '재귀'),
       (51, 'arbitrary_precision', '임의 정밀도 / 큰 수 연산'),
       (52, 'dag', '방향 비순환 그래프'),
       (53, 'coordinate_compression', '값 / 좌표 압축'),
       (54, 'euclidean', '유클리드 호제법'),
       (55, 'mst', '최소 스패닝 트리'),
       (56, 'precomputation', '런타임 전의 전처리'),
       (57, 'convex_hull', '볼록 껍질'),
       (58, 'sieve', '에라토스테네스의 체'),
       (59, 'topological_sorting', '위상 정렬'),
       (60, 'linear_algebra', '선형대수학'),
       (61, 'bipartite_matching', '이분 매칭'),
       (62, 'inclusion_and_exclusion', '포함 배제의 원리'),
       (63, 'lca', '최소 공통 조상'),
       (64, 'hashing', '해싱'),
       (65, 'floyd_warshall', '플로이드–워셜'),
       (66, 'sparse_table', '희소 배열'),
       (67, 'randomization', '무작위화'),
       (68, 'scc', '강한 연결 요소'),
       (69, 'grid_graph', '격자 그래프'),
       (70, 'modular_multiplicative_inverse', '모듈로 곱셈 역원'),
       (71, 'line_intersection', '선분 교차 판정'),
       (72, 'smaller_to_larger', '작은 집합에서 큰 집합으로 합치는 테크닉'),
       (73, 'fft', '고속 푸리에 변환'),
       (74, 'trie', '트라이'),
       (75, 'sqrt_decomposition', '제곱근 분할법'),
       (76, 'deque', '덱'),
       (77, 'calculus', '미적분학'),
       (78, 'geometry_3d', '3차원 기하학'),
       (79, 'ternary_search', '삼분 탐색'),
       (80, 'heuristics', '휴리스틱'),
       (81, 'mcmf', '최소 비용 최대 유량'),
       (82, 'suffix_array', '접미사 배열과 LCP 배열'),
       (83, 'sliding_window', '슬라이딩 윈도우'),
       (84, 'traceback', '역추적'),
       (85, 'sprague_grundy', '스프라그–그런디 정리'),
       (86, 'cht', '볼록 껍질을 이용한 최적화'),
       (87, 'euler_tour_technique', '오일러 경로 테크닉'),
       (88, 'centroid', '센트로이드'),
       (89, 'mitm', '중간에서 만나기'),
       (90, 'bitset', '비트 집합'),
       (91, 'pythagoras', '피타고라스 정리'),
       (92, 'permutation_cycle_decomposition', '순열 사이클 분할'),
       (93, 'kmp', 'KMP'),
       (94, 'lis', '가장 긴 증가하는 부분 수열 문제'),
       (95, 'gaussian_elimination', '가우스 소거법'),
       (96, 'parity', '홀짝성'),
       (97, 'hld', 'Heavy-light 분할'),
       (98, 'polygon_area', '다각형의 넓이'),
       (99, 'linearity_of_expectation', '기댓값의 선형성'),
       (100, 'mfmc', '최대 유량 최소 컷 정리'),
       (101, 'prime_factorization', '소인수분해'),
       (102, 'centroid_decomposition', '센트로이드 분할'),
       (103, 'bipartite_graph', '이분 그래프'),
       (104, 'flt', '페르마의 소정리'),
       (105, 'physics', '물리학'),
       (106, 'eulerian_path', '오일러 경로'),
       (107, '2_sat', '2-sat'),
       (108, 'queue', '큐'),
       (109, '0_1_bfs', '0-1 너비 우선 탐색'),
       (110, 'articulation', '단절점과 단절선'),
       (111, 'tsp', '외판원 순회 문제'),
       (112, 'difference_array', '차분 배열 트릭'),
       (113, 'flood_fill', '플러드 필'),
       (114, 'pigeonhole_principle', '비둘기집 원리'),
       (115, 'bcc', '이중 연결 요소'),
       (116, 'pst', '퍼시스턴트 세그먼트 트리'),
       (117, 'euler_phi', '오일러 피 함수'),
       (118, 'planar_graph', '평면 그래프'),
       (119, 'point_in_convex_polygon', '볼록 다각형 내부의 점 판정'),
       (120, 'crt', '중국인의 나머지 정리'),
       (121, 'deque_trick', '덱을 이용한 구간 최댓값 트릭'),
       (122, 'linked_list', '연결 리스트'),
       (123, 'functional_graph', '함수형 그래프'),
       (124, 'cactus', '선인장'),
       (125, 'bellman_ford', '벨만–포드'),
       (126, 'dp_digit', '자릿수를 이용한 다이나믹 프로그래밍'),
       (127, 'splay_tree', '스플레이 트리'),
       (128, 'divide_and_conquer_optimization', '분할 정복을 사용한 최적화'),
       (129, 'mo', 'mo''s'),
       (130, 'extended_euclidean', '확장 유클리드 호제법'),
       (131, 'rerooting', '트리에서의 전방향 다이나믹 프로그래밍'),
       (132, 'half_plane_intersection', '반평면 교집합'),
       (133, 'pbs', '병렬 이분 탐색'),
       (134, 'generating_function', '생성 함수'),
       (135, 'rotating_calipers', '회전하는 캘리퍼스'),
       (136, 'euler_characteristic', '오일러 지표 (2, χ=V-E+F)'),
       (137, 'regex', '정규 표현식'),
       (138, 'aho_corasick', '아호-코라식'),
       (139, 'slope_trick', '함수 개형을 이용한 최적화'),
       (140, 'multi_segtree', '다차원 세그먼트 트리'),
       (141, 'tree_diameter', '트리의 지름'),
       (142, 'harmonic_number', '조화수'),
       (143, 'dp_sum_over_subsets', '부분집합의 합 다이나믹 프로그래밍'),
       (144, 'dp_deque', '덱을 이용한 다이나믹 프로그래밍'),
       (145, 'manacher', '매내처'),
       (146, 'invariant', '불변량 찾기'),
       (147, 'miller_rabin', '밀러–라빈 소수 판별법'),
       (148, 'mobius_inversion', '뫼비우스 반전 공식'),
       (149, 'pollard_rho', '폴라드 로'),
       (150, 'angle_sorting', '각도 정렬'),
       (151, 'tree_isomorphism', '트리 동형 사상'),
       (152, 'merge_sort_tree', '머지 소트 트리'),
       (153, 'maximum_subarray', '최대 부분 배열 문제'),
       (154, 'point_in_non_convex_polygon', '오목 다각형 내부의 점 판정'),
       (155, 'simulated_annealing', '담금질 기법'),
       (156, 'dp_connection_profile', '커넥션 프로파일을 이용한 다이나믹 프로그래밍'),
       (157, 'lcs', '최장 공통 부분 수열 문제'),
       (158, 'link_cut_tree', '링크/컷 트리'),
       (159, 'berlekamp_massey', '벌리캠프–매시'),
       (160, 'hall', '홀의 결혼 정리'),
       (161, 'rabin_karp', '라빈–카프'),
       (162, 'numerical_analysis', '수치해석'),
       (163, 'statistics', '통계학'),
       (164, 'offline_dynamic_connectivity', '오프라인 동적 연결성 판정'),
       (165, 'z', 'z'),
       (166, 'cartesian_tree', '데카르트 트리'),
       (167, 'hungarian', '헝가리안'),
       (168, 'tree_compression', '트리 압축'),
       (169, 'alien', 'Aliens 트릭'),
       (170, 'linear_programming', '선형 계획법'),
       (171, 'geometric_boolean_operations', '도형에서의 불 연산'),
       (172, 'lucas', '뤼카 정리'),
       (173, 'voronoi', '보로노이 다이어그램'),
       (174, 'circulation', '서큘레이션'),
       (175, 'green', '그린 정리'),
       (176, 'dual_graph', '쌍대 그래프'),
       (177, 'beats', '세그먼트 트리 비츠'),
       (178, 'duality', '쌍대성'),
       (179, 'li_chao_tree', '리–차오 트리'),
       (180, 'general_matching', '일반적인 매칭'),
       (181, 'polynomial_interpolation', '다항식 보간법'),
       (182, 'monotone_queue_optimization', '단조 큐를 이용한 최적화'),
       (183, 'pick', '픽의 정리'),
       (184, 'matroid', '매트로이드'),
       (185, 'cdq', 'cdq 분할 정복'),
       (186, 'kitamasa', '다항식을 이용한 선형점화식 계산'),
       (187, 'xor_basis', '배타적 논리합 기저 (2, gf(2, 2))'),
       (188, 'discrete_log', '이산 로그'),
       (189, 'geometry_hyper', '4차원 이상의 기하학'),
       (190, 'tree_decomposition', '트리 분할'),
       (191, 'burnside', '번사이드 보조정리'),
       (192, 'degree_sequence', '차수열'),
       (193, 'min_enclosing_circle', '최소 외접원'),
       (194, 'utf8', 'utf-8 입력 처리'),
       (195, 'bulldozer', 'bulldozer 트릭'),
       (196, 'suffix_tree', '접미사 트리'),
       (197, 'bidirectional_search', '양방향 탐색'),
       (198, 'differential_cryptanalysis', '차분 공격'),
       (199, 'dominator_tree', '도미네이터 트리'),
       (200, 'palindrome_tree', '회문 트리'),
       (201, 'bayes', '베이즈 정리'),
       (202, 'pisano', '피사노 주기'),
       (203, 'knuth_x', '크누스 X'),
       (204, 'top_tree', '탑 트리'),
       (205, 'dancing_links', '춤추는 링크'),
       (206, 'stable_marriage', '안정 결혼 문제'),
       (207, 'lgv', '린드스트롬–게셀–비엔노 보조정리'),
       (208, 'rope', '로프'),
       (209, 'gradient_descent', '경사 하강법'),
       (210, 'knuth', '크누스 최적화'),
       (211, 'delaunay', '델로네 삼각분할'),
       (212, 'floor_sum', '유리 등차수열의 내림 합'),
       (213, 'bitset_lcs', '비트 집합을 이용한 최장 공통 부분 수열 최적화'),
       (214, 'birthday', '생일 문제'),
       (215, 'hirschberg', '히르쉬버그'),
       (216, 'chordal_graph', '현 그래프'),
       (217, 'discrete_sqrt', '이산 제곱근'),
       (218, 'multipoint_evaluation', '다중 대입값 계산'),
       (219, 'lte', '지수승강 보조정리'),
       (220, 'directed_mst', '유향 최소 스패닝 트리'),
       (221, 'stoer_wagner', '스토어–바그너'),
       (222, 'hackenbush', '하켄부시 게임'),
       (223, 'dial', '다이얼'),
       (224, 'majority_vote', '보이어–무어 다수결 투표'),
       (225, 'kinetic_segtree', '키네틱 세그먼트 트리'),
       (226, 'rb_tree', '레드-블랙 트리'),
       (227, 'a_star', 'a*'),
       (228, 'treewidth', '제한된 트리 너비'),
       (229, 'discrete_kth_root', '이산 k제곱근');

-- CSV 로드 (컨테이너 안 경로 기준)

LOAD DATA LOCAL INFILE '/Users/leejaehoon/Documents/sideprojects/solta/server/src/main/resources/csv/problem.csv'
    INTO TABLE problem
    FIELDS TERMINATED BY ','
    ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    (id, title, boj_problem_id, tier);

LOAD DATA LOCAL INFILE '/Users/leejaehoon/Documents/sideprojects/solta/server/src/main/resources/csv/problem_tag.csv'
    INTO TABLE problem_tag
    FIELDS TERMINATED BY ','
    LINES TERMINATED BY '\n'
    (problem_id, tag_id);

UPDATE problem
SET created_at = NOW(),
    updated_at = NOW()
WHERE 1 = 1;

UPDATE tag
SET created_at = NOW(),
    updated_at = NOW()
WHERE 1 = 1;

-- 테스트용 Member 추가 (dlwogns3413)
INSERT INTO member (id, name, github_id, boj_id, avatar_url, created_at, updated_at)
VALUES (1, 'dlwogns3413', 12345678, 'dlwogns3413', 'https://avatars.githubusercontent.com/u/12345678?v=4', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = name;

-- 테스트용 Solved 데이터 추가 (그래프 테스트용)
-- 최근 7일 데이터 (일별) - 문제가 있는 경우에만 실행
INSERT INTO solved (solve_time_seconds, solve_type, member_id, problem_id, created_at, updated_at)
SELECT 
    FLOOR(300 + RAND() * 1800) as solve_time_seconds, -- 5분 ~ 30분
    CASE WHEN RAND() > 0.3 THEN 'SELF' ELSE 'SOLUTION' END as solve_type,
    1 as member_id,
    (SELECT id FROM problem WHERE tier IN ('B1', 'B2', 'B3', 'S1', 'S2', 'G1', 'G2') ORDER BY RAND() LIMIT 1) as problem_id,
    DATE_SUB(NOW(), INTERVAL 1 DAY) as created_at,
    NOW() as updated_at
FROM (SELECT 1 as n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) as t
WHERE EXISTS (SELECT 1 FROM problem LIMIT 1);

INSERT INTO solved (solve_time_seconds, solve_type, member_id, problem_id, created_at, updated_at)
SELECT 
    FLOOR(400 + RAND() * 2000) as solve_time_seconds,
    CASE WHEN RAND() > 0.3 THEN 'SELF' ELSE 'SOLUTION' END as solve_type,
    1 as member_id,
    (SELECT id FROM problem WHERE tier IN ('B1', 'B2', 'S1', 'S2', 'G1') ORDER BY RAND() LIMIT 1) as problem_id,
    DATE_SUB(NOW(), INTERVAL 2 DAY) as created_at,
    NOW() as updated_at
FROM (SELECT 1 as n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) as t
WHERE EXISTS (SELECT 1 FROM problem LIMIT 1);

INSERT INTO solved (solve_time_seconds, solve_type, member_id, problem_id, created_at, updated_at)
SELECT 
    FLOOR(200 + RAND() * 1500) as solve_time_seconds,
    CASE WHEN RAND() > 0.3 THEN 'SELF' ELSE 'SOLUTION' END as solve_type,
    1 as member_id,
    (SELECT id FROM problem WHERE tier IN ('B1', 'B2', 'B3', 'S1', 'S2', 'G1') ORDER BY RAND() LIMIT 1) as problem_id,
    DATE_SUB(NOW(), INTERVAL 3 DAY) as created_at,
    NOW() as updated_at
FROM (SELECT 1 as n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) as t
WHERE EXISTS (SELECT 1 FROM problem LIMIT 1);

INSERT INTO solved (solve_time_seconds, solve_type, member_id, problem_id, created_at, updated_at)
SELECT 
    FLOOR(350 + RAND() * 1900) as solve_time_seconds,
    CASE WHEN RAND() > 0.3 THEN 'SELF' ELSE 'SOLUTION' END as solve_type,
    1 as member_id,
    (SELECT id FROM problem WHERE tier IN ('B2', 'B3', 'S1', 'S2', 'G1', 'G2') ORDER BY RAND() LIMIT 1) as problem_id,
    DATE_SUB(NOW(), INTERVAL 4 DAY) as created_at,
    NOW() as updated_at
FROM (SELECT 1 as n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) as t
WHERE EXISTS (SELECT 1 FROM problem LIMIT 1);

INSERT INTO solved (solve_time_seconds, solve_type, member_id, problem_id, created_at, updated_at)
SELECT 
    FLOOR(250 + RAND() * 1600) as solve_time_seconds,
    CASE WHEN RAND() > 0.3 THEN 'SELF' ELSE 'SOLUTION' END as solve_type,
    1 as member_id,
    (SELECT id FROM problem WHERE tier IN ('B1', 'B2', 'S1', 'S2', 'G1') ORDER BY RAND() LIMIT 1) as problem_id,
    DATE_SUB(NOW(), INTERVAL 5 DAY) as created_at,
    NOW() as updated_at
FROM (SELECT 1 as n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) as t
WHERE EXISTS (SELECT 1 FROM problem LIMIT 1);

INSERT INTO solved (solve_time_seconds, solve_type, member_id, problem_id, created_at, updated_at)
SELECT 
    FLOOR(300 + RAND() * 1700) as solve_time_seconds,
    CASE WHEN RAND() > 0.3 THEN 'SELF' ELSE 'SOLUTION' END as solve_type,
    1 as member_id,
    (SELECT id FROM problem WHERE tier IN ('B1', 'B2', 'B3', 'S1', 'S2') ORDER BY RAND() LIMIT 1) as problem_id,
    DATE_SUB(NOW(), INTERVAL 6 DAY) as created_at,
    NOW() as updated_at
FROM (SELECT 1 as n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) as t
WHERE EXISTS (SELECT 1 FROM problem LIMIT 1);

INSERT INTO solved (solve_time_seconds, solve_type, member_id, problem_id, created_at, updated_at)
SELECT 
    FLOOR(280 + RAND() * 1400) as solve_time_seconds,
    CASE WHEN RAND() > 0.3 THEN 'SELF' ELSE 'SOLUTION' END as solve_type,
    1 as member_id,
    (SELECT id FROM problem WHERE tier IN ('B1', 'B2', 'S1', 'S2', 'G1') ORDER BY RAND() LIMIT 1) as problem_id,
    DATE_SUB(NOW(), INTERVAL 7 DAY) as created_at,
    NOW() as updated_at
FROM (SELECT 1 as n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) as t
WHERE EXISTS (SELECT 1 FROM problem LIMIT 1);

-- 최근 30일 데이터 (일별, 7일 이후)
INSERT INTO solved (solve_time_seconds, solve_type, member_id, problem_id, created_at, updated_at)
SELECT 
    FLOOR(400 + RAND() * 2000) as solve_time_seconds,
    CASE WHEN RAND() > 0.3 THEN 'SELF' ELSE 'SOLUTION' END as solve_type,
    1 as member_id,
    (SELECT id FROM problem WHERE tier IN ('B1', 'B2', 'B3', 'B4', 'S1', 'S2', 'S3', 'G1', 'G2') ORDER BY RAND() LIMIT 1) as problem_id,
    DATE_SUB(NOW(), INTERVAL 8 + (t.n * 2) DAY) as created_at,
    NOW() as updated_at
FROM (SELECT 0 as n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) as t
CROSS JOIN (SELECT 1 as c UNION SELECT 2 UNION SELECT 3) as c
WHERE EXISTS (SELECT 1 FROM problem LIMIT 1);

-- 최근 3개월 데이터 (주별)
INSERT INTO solved (solve_time_seconds, solve_type, member_id, problem_id, created_at, updated_at)
SELECT 
    FLOOR(500 + RAND() * 2500) as solve_time_seconds,
    CASE WHEN RAND() > 0.3 THEN 'SELF' ELSE 'SOLUTION' END as solve_type,
    1 as member_id,
    (SELECT id FROM problem WHERE tier IN ('B1', 'B2', 'B3', 'B4', 'B5', 'S1', 'S2', 'S3', 'S4', 'G1', 'G2', 'G3') ORDER BY RAND() LIMIT 1) as problem_id,
    DATE_SUB(NOW(), INTERVAL 30 + (t.n * 7) DAY) as created_at,
    NOW() as updated_at
FROM (SELECT 0 as n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11) as t
CROSS JOIN (SELECT 1 as c UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8) as c
WHERE EXISTS (SELECT 1 FROM problem LIMIT 1);

-- 최근 6개월 데이터 (주별, 3개월 이후)
INSERT INTO solved (solve_time_seconds, solve_type, member_id, problem_id, created_at, updated_at)
SELECT 
    FLOOR(600 + RAND() * 3000) as solve_time_seconds,
    CASE WHEN RAND() > 0.3 THEN 'SELF' ELSE 'SOLUTION' END as solve_type,
    1 as member_id,
    (SELECT id FROM problem WHERE tier IN ('B1', 'B2', 'B3', 'B4', 'B5', 'S1', 'S2', 'S3', 'S4', 'S5', 'G1', 'G2', 'G3', 'G4') ORDER BY RAND() LIMIT 1) as problem_id,
    DATE_SUB(NOW(), INTERVAL 90 + (t.n * 7) DAY) as created_at,
    NOW() as updated_at
FROM (SELECT 0 as n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) as t
CROSS JOIN (SELECT 1 as c UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) as c
WHERE EXISTS (SELECT 1 FROM problem LIMIT 1);

-- 전체 데이터 (월별, 6개월 이전)
INSERT INTO solved (solve_time_seconds, solve_type, member_id, problem_id, created_at, updated_at)
SELECT 
    FLOOR(800 + RAND() * 3500) as solve_time_seconds,
    CASE WHEN RAND() > 0.3 THEN 'SELF' ELSE 'SOLUTION' END as solve_type,
    1 as member_id,
    (SELECT id FROM problem WHERE tier IN ('B1', 'B2', 'B3', 'B4', 'B5', 'S1', 'S2', 'S3', 'S4', 'S5', 'G1', 'G2', 'G3', 'G4', 'G5', 'P1', 'P2') ORDER BY RAND() LIMIT 1) as problem_id,
    DATE_SUB(NOW(), INTERVAL 180 + (t.n * 30) DAY) as created_at,
    NOW() as updated_at
FROM (SELECT 0 as n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11) as t
CROSS JOIN (SELECT 1 as c UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18) as c
WHERE EXISTS (SELECT 1 FROM problem LIMIT 1);
