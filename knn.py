
data_file = open('./swipedata/recordlog.txt', 'r')
lines = data_file.readlines()
curr_swipe = 0
last_swipe = -1
for line in lines:
    if curr_swipe != last_swipe:
        last_swipe += 1


    if line == 'New Swipe':
        curr_swipe += 1