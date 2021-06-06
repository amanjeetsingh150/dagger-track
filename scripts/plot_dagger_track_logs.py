import argparse
import re
from dependency_plotter import Plotter


# Creates a dict like {' me.amanjeet.daggertrack.DaggerTrackApp': ' 1ms'} for each clock times
def get_clock_dict(clock_logs, pattern):
    wall_clock_dict = {}
    for log in clock_logs:
        colon_separated_log = re.search(pattern, log)
        if colon_separated_log is not None:
            clock_log = colon_separated_log.group(1).split(':')
            wall_clock_dict.update({
                clock_log[i]: clock_log[i + 1] for i in range(0, len(clock_log), 2)
            })
    return wall_clock_dict


def read_time(raw_dagger_track_logs):
    wall_clock_logs = re.findall("DaggerTrack: Total time of .*", raw_dagger_track_logs)
    on_cpu_clock_logs = re.findall("DaggerTrack: Total On CPU time of .*", raw_dagger_track_logs)
    off_cpu_clock_logs = re.findall("DaggerTrack: Total Off CPU time of .*", raw_dagger_track_logs)
    wall_clock_dict = get_clock_dict(wall_clock_logs, "Total time of(.*)")
    on_cpu_clock_dict = get_clock_dict(on_cpu_clock_logs, "Total On CPU time of(.*)")
    off_cpu_clock_dict = get_clock_dict(off_cpu_clock_logs, "Total Off CPU time of(.*)")

    Plotter(
        wall_clock_dict,
        on_cpu_clock_dict,
        off_cpu_clock_dict
    ).plot_deps()


parser = argparse.ArgumentParser(description='Get dependency view from DaggerTrack')
parser.add_argument('--logs', action='store', type=str, required=True)

args = parser.parse_args()
log_file_path = args.logs

dagger_track_log_file = open(log_file_path, "r+")
raw_logs = dagger_track_log_file.read()

read_time(raw_logs)
