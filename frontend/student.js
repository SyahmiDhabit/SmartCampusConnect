async function loadStudents() {

    try {

        const response =
            await fetch("http://localhost:8081/api/students");

        const data =
            await response.json();

        let html = "<h3>Student List</h3>";

        data.forEach(student => {

            html += `
                <div>
                    <b>${student.id}</b> |
                    ${student.name} |
                    ${student.programme} |
                    GPA: ${student.gpa}
                </div>
                <br>
            `;

        });

        document.getElementById("students").innerHTML =
            html;

    }
    catch(error) {

        document.getElementById("students").innerHTML =
            error.toString();

    }

}