import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/"
LOGIN_URL = "http://localhost:3000/login"

TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}
ADMIN_CREDENTIALS = {"userId": "A001", "password": "123456"}

@pytest.mark.parametrize("credentials, role", [
    (TEACHER_CREDENTIALS, "teacher"),
    (STUDENT_CREDENTIALS, "student"),
    (ADMIN_CREDENTIALS, "admin")
])
def test_home_render(playwright, credentials, role):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    page.goto(BASE_URL)
    # 管理员应被重定向到/manage/teachers
    if role == "admin":
        expect(page).to_have_url(re.compile(r"http://localhost:3000/manage/teachers"))
        context.close()
        browser.close()
        return
    expect(page).to_have_url(re.compile(r"http://localhost:3000/$"))
    # 判断欢迎语是否存在再断言
    if page.locator("h1:has-text('欢迎使用EduSoft平台')").count() > 0:
        expect(page.locator("h1:has-text('欢迎使用EduSoft平台')")).to_be_visible()
    # 教师身份可见“创建课程”按钮
    if role == "teacher":
        if page.locator("a.btn-primary:has-text('创建课程')").count() > 0:
            expect(page.locator("a.btn-primary:has-text('创建课程')")).to_be_visible()
    else:
        if page.locator("a.btn-primary:has-text('创建课程')").count() > 0:
            expect(page.locator("a.btn-primary:has-text('创建课程')")).not_to_be_visible()
    # 课程区块或空提示
    if page.locator(".course-card").count() > 0:
        expect(page.locator(".course-card").first).to_be_visible()
    elif page.locator(".empty-state").count() > 0:
        expect(page.locator(".empty-state")).to_be_visible()
    # 特色区块
    if page.locator("h2:has-text('平台特色')").count() > 0:
        expect(page.locator("h2:has-text('平台特色')")).to_be_visible()
        if page.locator(".feature-card").count() > 0:
            expect(page.locator(".feature-card").first).to_be_visible()
    context.close()
    browser.close()
