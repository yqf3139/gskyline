import pandas
import json

if __name__ == '__main__':
    rawfile = open('leagueleaders.json')
    records = json.loads(rawfile.read())
    rawfile.close()
    players = records['resultSet']['rowSet']

    # Points (PTS), Rebounds (REB), Assists (AST), Steals (STL), and Blocks (BLK)
    colums = [[p[21], p[15], p[16], p[17], p[18]] for p in players]

    dataset = pandas.read_json(json.dumps(colums))
    # pd = dataset.fillna(dataset.mean())
    pd = dataset.interpolate()
    pd = -pd
    print(pd)
    outfile = open('nba_{}.txt'.format(len(pd.columns)), 'w')
    outfile.write(pd.to_csv(header=False, index=False, sep=' '))
    outfile.close()
