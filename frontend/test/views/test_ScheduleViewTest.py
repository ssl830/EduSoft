import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/schedule"
LOGIN_URL = "http://localhost:3000/login"

TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}
ADMIN_CREDENTIALS = {"userId": "A001", "password": "123456"}

@pytest.mark.parametrize("credentials, role", [
    (TEACHER_CREDENTIALS, "teacher"),
    (STUDENT_CREDENTIALS, "student"),
    (ADMIN_CREDENTIALS, "admin")
])
def test_schedule_view_render(playwright, credentials, role):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    page.goto(BASE_URL)
    if role == "admin":
        expect(page).to_have_url(re.compile(r"http://localhost:3000/manage/teachers"))
        context.close()
        browser.close()
        return
    if role == "teacher":
        expect(page).to_have_url(re.compile(r"http://localhost:3000/$"))
        context.close()
        browser.close()
        return
    expect(page).to_have_url(re.compile(r"http://localhost:3000/schedule"))
    # 标题断言
    expect(page.locator("h2.schedule-title")).to_have_text("我的课表")
    # 周选择器
    expect(page.locator(".week-selector")).to_be_visible()
    # 课表网格或空提示
    if page.locator(".schedule-grid").count() > 0:
        expect(page.locator(".schedule-grid")).to_be_visible()
    if page.locator(".error-message").count() > 0:
        expect(page.locator(".error-message")).to_be_visible()
    context.close()
    browser.close()
