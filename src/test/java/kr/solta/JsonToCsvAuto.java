package kr.solta;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import kr.solta.domain.Tier;

public class JsonToCsvAuto {

    public static void main(String[] args) throws Exception {
        File folder = new File("/Users/leejaehoon/Downloads/solvedac-main/data");
        FileWriter problemCsv = new FileWriter("/Users/leejaehoon/Downloads/problem.csv");
        FileWriter problemTagCsv = new FileWriter("/Users/leejaehoon/Downloads/problem_tag.csv");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Integer> tagMap = loadTagMap();

        long[] problemAutoId = {1}; // 재귀에서 증가 가능하도록 배열 사용

        processFolder(folder, mapper, tagMap, problemCsv, problemTagCsv, problemAutoId);

        problemCsv.close();
        problemTagCsv.close();

        System.out.println("CSV 생성 완료!");
    }

    private static void processFolder(File folder, ObjectMapper mapper, Map<String, Integer> tagMap,
                                      FileWriter problemCsv, FileWriter problemTagCsv, long[] problemAutoId) {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        // 이름 순 정렬
        Arrays.sort(files, Comparator.comparing(File::getName));

        for (File file : files) {
            if (file.isDirectory()) {
                processFolder(file, mapper, tagMap, problemCsv, problemTagCsv, problemAutoId);
            } else if (file.getName().endsWith(".json")) {
                try {
                    JsonNode root = mapper.readTree(file);
                    
                    String title = "";
                    if (root.has("titleKo") && !root.get("titleKo").asText().isEmpty()) {
                        title = root.get("titleKo").asText();
                    } else if (root.has("title")) {
                        title = root.get("title").asText();
                    }
                    title = title.replace("\"", "\"\""); // CSV 안전하게

                    long bojProblemId = root.get("problemId").asLong();
                    int level = root.get("level").asInt();
                    String tier = mapLevelToTier(level);

                    problemCsv.write(String.format("%d,\"%s\",%d,%s,%d\n",
                            problemAutoId[0], title, bojProblemId, tier, level));

                    JsonNode tags = root.get("tags");
                    if (tags != null) {
                        for (JsonNode tagNode : tags) {
                            String tagKey = tagNode.get("key").asText();
                            Integer tagId = tagMap.get(tagKey);
                            if (tagId != null) {
                                problemTagCsv.write(String.format("%d,%d\n", problemAutoId[0], tagId));
                            }
                        }
                    }

                    problemAutoId[0]++;
                } catch (Exception e) {
                    System.err.println("파일 처리 중 오류: " + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }
    }

    private static Map<String, Integer> loadTagMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("math", 1);
        map.put("implementation", 2);
        map.put("dp", 3);
        map.put("graphs", 4);
        map.put("data_structures", 5);
        map.put("greedy", 6);
        map.put("string", 7);
        map.put("bruteforcing", 8);
        map.put("graph_traversal", 9);
        map.put("sorting", 10);
        map.put("ad_hoc", 11);
        map.put("geometry", 12);
        map.put("trees", 13);
        map.put("number_theory", 14);
        map.put("segtree", 15);
        map.put("binary_search", 16);
        map.put("set", 17);
        map.put("constructive", 18);
        map.put("simulation", 19);
        map.put("arithmetic", 20);
        map.put("prefix_sum", 21);
        map.put("combinatorics", 22);
        map.put("bfs", 23);
        map.put("case_work", 24);
        map.put("dfs", 25);
        map.put("shortest_path", 26);
        map.put("bitmask", 27);
        map.put("hash_set", 28);
        map.put("dijkstra", 29);
        map.put("backtracking", 30);
        map.put("sweeping", 31);
        map.put("disjoint_set", 32);
        map.put("dp_tree", 33);
        map.put("tree_set", 34);
        map.put("parsing", 35);
        map.put("priority_queue", 36);
        map.put("divide_and_conquer", 37);
        map.put("parametric_search", 38);
        map.put("two_pointer", 39);
        map.put("game_theory", 40);
        map.put("stack", 41);
        map.put("probability", 42);
        map.put("primality_test", 43);
        map.put("flow", 44);
        map.put("lazyprop", 45);
        map.put("dp_bitfield", 46);
        map.put("exponentiation_by_squaring", 47);
        map.put("offline_queries", 48);
        map.put("knapsack", 49);
        map.put("recursion", 50);
        map.put("arbitrary_precision", 51);
        map.put("dag", 52);
        map.put("coordinate_compression", 53);
        map.put("euclidean", 54);
        map.put("mst", 55);
        map.put("precomputation", 56);
        map.put("convex_hull", 57);
        map.put("sieve", 58);
        map.put("topological_sorting", 59);
        map.put("linear_algebra", 60);
        map.put("bipartite_matching", 61);
        map.put("inclusion_and_exclusion", 62);
        map.put("lca", 63);
        map.put("hashing", 64);
        map.put("floyd_warshall", 65);
        map.put("sparse_table", 66);
        map.put("randomization", 67);
        map.put("scc", 68);
        map.put("grid_graph", 69);
        map.put("modular_multiplicative_inverse", 70);
        map.put("line_intersection", 71);
        map.put("smaller_to_larger", 72);
        map.put("fft", 73);
        map.put("trie", 74);
        map.put("sqrt_decomposition", 75);
        map.put("deque", 76);
        map.put("calculus", 77);
        map.put("geometry_3d", 78);
        map.put("ternary_search", 79);
        map.put("heuristics", 80);
        map.put("mcmf", 81);
        map.put("suffix_array", 82);
        map.put("sliding_window", 83);
        map.put("traceback", 84);
        map.put("sprague_grundy", 85);
        map.put("cht", 86);
        map.put("euler_tour_technique", 87);
        map.put("centroid", 88);
        map.put("mitm", 89);
        map.put("bitset", 90);
        map.put("pythagoras", 91);
        map.put("permutation_cycle_decomposition", 92);
        map.put("kmp", 93);
        map.put("lis", 94);
        map.put("gaussian_elimination", 95);
        map.put("parity", 96);
        map.put("hld", 97);
        map.put("polygon_area", 98);
        map.put("linearity_of_expectation", 99);
        map.put("mfmc", 100);
        map.put("prime_factorization", 101);
        map.put("centroid_decomposition", 102);
        map.put("bipartite_graph", 103);
        map.put("flt", 104);
        map.put("physics", 105);
        map.put("eulerian_path", 106);
        map.put("2_sat", 107);
        map.put("queue", 108);
        map.put("0_1_bfs", 109);
        map.put("articulation", 110);
        map.put("tsp", 111);
        map.put("difference_array", 112);
        map.put("flood_fill", 113);
        map.put("pigeonhole_principle", 114);
        map.put("bcc", 115);
        map.put("pst", 116);
        map.put("euler_phi", 117);
        map.put("planar_graph", 118);
        map.put("point_in_convex_polygon", 119);
        map.put("crt", 120);
        map.put("deque_trick", 121);
        map.put("linked_list", 122);
        map.put("functional_graph", 123);
        map.put("cactus", 124);
        map.put("bellman_ford", 125);
        map.put("dp_digit", 126);
        map.put("splay_tree", 127);
        map.put("divide_and_conquer_optimization", 128);
        map.put("mo", 129);
        map.put("extended_euclidean", 130);
        map.put("rerooting", 131);
        map.put("half_plane_intersection", 132);
        map.put("pbs", 133);
        map.put("generating_function", 134);
        map.put("rotating_calipers", 135);
        map.put("euler_characteristic", 136);
        map.put("regex", 137);
        map.put("aho_corasick", 138);
        map.put("slope_trick", 139);
        map.put("multi_segtree", 140);
        map.put("tree_diameter", 141);
        map.put("harmonic_number", 142);
        map.put("dp_sum_over_subsets", 143);
        map.put("dp_deque", 144);
        map.put("manacher", 145);
        map.put("invariant", 146);
        map.put("miller_rabin", 147);
        map.put("mobius_inversion", 148);
        map.put("pollard_rho", 149);
        map.put("angle_sorting", 150);
        map.put("tree_isomorphism", 151);
        map.put("merge_sort_tree", 152);
        map.put("maximum_subarray", 153);
        map.put("point_in_non_convex_polygon", 154);
        map.put("simulated_annealing", 155);
        map.put("dp_connection_profile", 156);
        map.put("lcs", 157);
        map.put("link_cut_tree", 158);
        map.put("berlekamp_massey", 159);
        map.put("hall", 160);
        map.put("rabin_karp", 161);
        map.put("numerical_analysis", 162);
        map.put("statistics", 163);
        map.put("offline_dynamic_connectivity", 164);
        map.put("z", 165);
        map.put("cartesian_tree", 166);
        map.put("hungarian", 167);
        map.put("tree_compression", 168);
        map.put("alien", 169);
        map.put("linear_programming", 170);
        map.put("geometric_boolean_operations", 171);
        map.put("lucas", 172);
        map.put("voronoi", 173);
        map.put("circulation", 174);
        map.put("green", 175);
        map.put("dual_graph", 176);
        map.put("beats", 177);
        map.put("duality", 178);
        map.put("li_chao_tree", 179);
        map.put("general_matching", 180);
        map.put("polynomial_interpolation", 181);
        map.put("monotone_queue_optimization", 182);
        map.put("pick", 183);
        map.put("matroid", 184);
        map.put("cdq", 185);
        map.put("kitamasa", 186);
        map.put("xor_basis", 187);
        map.put("discrete_log", 188);
        map.put("geometry_hyper", 189);
        map.put("tree_decomposition", 190);
        map.put("burnside", 191);
        map.put("degree_sequence", 192);
        map.put("min_enclosing_circle", 193);
        map.put("utf8", 194);
        map.put("bulldozer", 195);
        map.put("suffix_tree", 196);
        map.put("bidirectional_search", 197);
        map.put("differential_cryptanalysis", 198);
        map.put("dominator_tree", 199);
        map.put("palindrome_tree", 200);
        map.put("bayes", 201);
        map.put("pisano", 202);
        map.put("knuth_x", 203);
        map.put("top_tree", 204);
        map.put("dancing_links", 205);
        map.put("stable_marriage", 206);
        map.put("lgv", 207);
        map.put("rope", 208);
        map.put("gradient_descent", 209);
        map.put("knuth", 210);
        map.put("delaunay", 211);
        map.put("floor_sum", 212);
        map.put("bitset_lcs", 213);
        map.put("birthday", 214);
        map.put("hirschberg", 215);
        map.put("chordal_graph", 216);
        map.put("discrete_sqrt", 217);
        map.put("multipoint_evaluation", 218);
        map.put("lte", 219);
        map.put("directed_mst", 220);
        map.put("stoer_wagner", 221);
        map.put("hackenbush", 222);
        map.put("dial", 223);
        map.put("majority_vote", 224);
        map.put("kinetic_segtree", 225);
        map.put("rb_tree", 226);
        map.put("a_star", 227);
        map.put("treewidth", 228);
        map.put("discrete_kth_root", 229);
        return map;
    }

    private static String mapLevelToTier(int level) {
        return Tier.getTier(level).toString();
    }
}
