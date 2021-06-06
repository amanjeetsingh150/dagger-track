from graphviz import Digraph


class Plotter:
    def __init__(self, wall_clock_dict, on_cpu_clock_dict, off_cpu_clock_dict):
        self.wall_clock_dict = wall_clock_dict
        self.on_cpu_clock_dict = on_cpu_clock_dict
        self.off_cpu_clock_dict = off_cpu_clock_dict

    def plot_deps(self):
        dot = Digraph(comment='Dependency graph from DaggerTrack')
        for clazz, time in self.wall_clock_dict.items():
            label = clazz + "\n" + "Wall Clock time: " + time
            label += "\n" + "On CPU time: " + self.on_cpu_clock_dict[clazz]
            label += "\n" + "Off CPU time: " + self.off_cpu_clock_dict[clazz]
            dot.node(name=clazz, label=label)
        key_list = list(self.wall_clock_dict.keys())
        for idx, val in enumerate(key_list):
            if idx < (len(key_list) - 1):
                dot.edge(key_list[idx], key_list[idx + 1], constraint='false')
        dot.render('output/dependency-time.gv', view=True)
