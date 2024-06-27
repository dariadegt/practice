from selenium import webdriver
from bs4 import BeautifulSoup
import requests
from tabulate import tabulate

# ссылки
base_url = 'https://www.championat.com/football/_euro/tournament/5754/calendar/'
main_url = 'https://www.championat.com/football/_euro/tournament/5754/calendar/'

driver = webdriver.Firefox()

driver.get(base_url)
soup = BeautifulSoup(driver.page_source, 'html.parser')

matches = soup.find_all('tr', class_='stat-results__row')
table_data = []

for match in matches[1:]:
    team_names = []
    for team in match.find_all('span', class_='table-item__name'):
        team_names.append(team.text.strip())

    if len(team_names) >= 2:
        team1, team2 = team_names[:2]
    else:
        team1 = team2 = 'Unknown'

    # вывод времени и даты
    score_element = match.find('td', class_='stat-results__count _order_3')
    td_el = match.find_all('td', class_='stat-results__date-time _hidden-td')
    if td_el:
        for td in td_el:
            date_time = td.text.strip()
            parts = date_time.split()
            date = parts[0]
            time = parts[1]
    else:
        date = time = 'Unknown'

    # вывод счета
    if score_element:
        score_span = score_element.find('span', class_='stat-results__count-main')
        if score_span:
            score = score_span.text.strip()
        else:
            score = 'Unknown'
    else:
        score = 'Unknown'
    table_data.append([f"{team1} - {team2}", date, time, score, ''])  

# ссылки на матчи
match_links = []
for row in soup.select('table tr'):
    link = row.find('a', href=True)
    if link and link['href'].startswith('/football/_euro/tournament/5754/match/'):
        match_links.append('https://www.championat.com' + link['href'] + '/#stats')

# проход по ссылкам матчей и вывод инф-ии
for i, match_link in enumerate(match_links, 1):
    match_driver = webdriver.Firefox()
    match_driver.get(match_link)
    match_soup = BeautifulSoup(match_driver.page_source, 'html.parser')


    bubbles = match_soup.find_all('div', class_='match-timeline__bubble-title')
    match_info = []

    for bubble in bubbles:
        score_element = bubble.find('div', class_='match-timeline__bubble-score')
        if score_element:
            score = score_element.text
            player_name = bubble.text.replace(score, '').strip()
            match_info.append(f"Score: {score}, Player: {player_name}")

    additional_data = "\n".join(match_info)
    if i-1 < len(table_data):
        table_data[i-1][4] = additional_data

    match_driver.quit()

# закрытие драйвера
driver.quit()

# вывод таблицы
headers = ["Match (Teams)", "Date", "Time", "Score", "Additional Data"]
print(tabulate(table_data, headers, tablefmt="grid"))