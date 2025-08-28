import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/learning-records"
LOGIN_URL = "http://localhost:3000/login"

TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}
ADMIN_CREDENTIALS = {"userId": "A001", "password": "123456"}

@pytest.mark.parametrize("credentials, role", [
    (TEACHER_CREDENTIALS, "teacher"),
    (STUDENT_CREDENTIALS, "student"),
    (ADMIN_CREDENTIALS, "admin")
])
def test_learning_records_render(playwright, credentials, role):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    page.goto(BASE_URL)
    if role == "teacher":
        expect(page).to_have_url(re.compile(r"http://localhost:3000/$"))
        context.close()
        browser.close()
        return
    if role == "admin":
        expect(page).to_have_url(re.compile(r"http://localhost:3000/manage/teachers"))
        context.close()
        browser.close()
        return
    expect(page).to_have_url(re.compile(r"http://localhost:3000/learning-records"))
    expect(page.locator("h1:has-text('练习记录')")).to_be_visible()
    expect(page.locator(".filter-section")).to_be_visible()
    # 检查课程列表或空提示
    if page.locator(".courses-container .course-item").count() > 0:
        expect(page.locator(".courses-container .course-item").first).to_be_visible()
    else:
        # 只有在页面加载完成且有 .empty-state 元素时才断言可见
        if page.locator(".empty-state").count() > 0:
            expect(page.locator(".empty-state")).to_be_visible()
    context.close()
    browser.close()
