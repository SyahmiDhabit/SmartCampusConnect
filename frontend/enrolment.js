async function enrolStudent() {

    try {

        const studentId =
            document.getElementById("studentId").value;

        const courseCode =
            document.getElementById("courseCode").value;

        const semester =
            document.getElementById("semester").value;

        const response = await fetch(
            "http://localhost:8082/api/enrolments",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    studentId: studentId,
                    courseCode: courseCode,
                    semester: semester
                })
            }
        );

        const data = await response.json();

        document.getElementById("result").innerHTML =
            "<h3>Enrolment Success</h3><pre>" +
            JSON.stringify(data, null, 2) +
            "</pre>";

    }
    catch(error) {

        document.getElementById("result").innerHTML =
            error.toString();

    }

}