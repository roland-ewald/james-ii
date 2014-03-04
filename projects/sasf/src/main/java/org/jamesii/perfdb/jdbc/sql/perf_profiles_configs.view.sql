SELECT RT.id as config_id, PM.performance_type_id as perf_tpye_id, MIN(PM.performance) as min_perf, AVG(PM.performance) as avg_perf,
MAX(PM.performance) as max_perf, COUNT(PM.performance) as sample_size, STDDEV_POP(PM.performance) as std_variation 
FROM runtime_configurations as RT LEFT JOIN (performance_measurements as PM) ON (PM.config_id=RT.id AND PM.performance >= 0) GROUP BY PM.config_id;