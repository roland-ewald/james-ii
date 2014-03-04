SELECT problems.id as problem_id, PM.performance_type_id as perf_type_id, MIN(PM.performance) as min_perf, AVG(PM.performance) as avg_perf,
MAX(PM.performance) as max_perf, COUNT(PM.performance) as sample_size, STDDEV_POP(PM.performance) as std_variation FROM problems LEFT JOIN 
(runtime_configurations as RT, performance_measurements as PM) ON (problems.id = RT.problem_id AND PM.config_id=RT.id AND PM.performance >= 0) 
GROUP BY problems.id, PM.performance_type_id;