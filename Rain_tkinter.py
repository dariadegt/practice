import tkinter as tk
from random import randint
#движения капель(при переходе за линию экрана, возвращ назад)
def move_raindrop(drop):
    drop['y'] += drop['yspeed']
    canvas.move(drop['line'], 0, drop['yspeed'])

    if drop['y'] >= window.winfo_width():
        canvas.move(drop['line'], 0, -(window.winfo_width() + drop['length'])) 
        drop['y'] -= window.winfo_width() + drop['length']
#отрисовка
def redraw():
    for drop in drops:
        move_raindrop(drop)

    window.after(10, redraw)
#Параметры окна + оставленный крестик закрытия
window = tk.Tk()
window.title("Дождь")
window.geometry("800x600")
window.attributes("-toolwindow", True)

canvas = tk.Canvas(window, width=600, height=800,bg='#9999FF')
canvas.pack()#для лучщего положения в окне

drops = []
for _ in range(300):
    x = randint(0, 800)
    y = randint(0, 600)
    yspeed = randint(2,7)
    length = randint(5, 20)
    color = 'blue'
    line = canvas.create_line(x, y, x, y + length, fill=color)
    drops.append({'x': x, 'y': y, 'yspeed': yspeed, 'length': length, 'line': line})

redraw()

window.mainloop()
