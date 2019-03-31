# whenever new analyzers or recommenders are tested, existing students are run through this new engine and it's checked if the recommended questions make sense
# for pick a few students and put their data in a toy mysql server to play with
from collections import defaultdict
import pymysql.cursors
import csv

database = 45
connection = pymysql.connect(host='ope-db.cae0huknrosy.us-east-1.rds.amazonaws.com',
                                 user='opemaster',
                                 password='YWFkYzU4ZDgxYzBh',
                                 db='ope_class_' + str(database),
                                 charset='utf8mb4',
                                 cursorclass=pymysql.cursors.DictCursor)


def findStudentProfile():
    # find good, bad and average students: studentProfileMap[user_id] = [#attempts, #correct]
    with connection.cursor() as cursor:
        studentCorrects = defaultdict(int)
        studentAttempts = defaultdict(int)
        sqlStatement = "select user_id, class_question_id, number_attempts, number_correct from student_response_report where number_attempts > 0"
        cursor.execute(sqlStatement)
        studentResponseReports = cursor.fetchall()
        for studentResponse in studentResponseReports:
            studentId = int(studentResponse["user_id"])
            attempts = int(studentResponse["number_attempts"])
            corrects = int(studentResponse["number_correct"])
            studentAttempts[studentId] += attempts
            studentCorrects[studentId] += corrects
        studentProfiles = [[studentId, attempts, studentCorrects[studentId], studentCorrects[studentId] / attempts] for studentId, attempts in studentAttempts.items() if attempts > 0]
        studentProfiles.sort(key=lambda x: x[3], reverse=True)
        # print(studentProfiles)

        with open('csvDumps/ope_class_' + str(database) + "_student_profile.csv", 'w', newline='') as studentProfileFile:
            wr = csv.writer(studentProfileFile, quoting=csv.QUOTE_ALL)
            wr.writerow(["studentId", "attempts", "corrects", "%correct"])
            for studentProfile in studentProfiles:
                wr.writerow(studentProfile)
        return studentProfiles
# bad student :  3471	872	26	0.02981651376146789
#                4670	831	18	0.021660649819494584

# good student:  4454	868	822	0.9470046082949308
#                5594	882	823	0.9331065759637188


def main():
    findStudentProfile()


if __name__ == "__main__":
    main()



