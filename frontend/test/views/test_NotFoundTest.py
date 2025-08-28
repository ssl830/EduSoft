import pytest
import re
from playwright.sync_api import expect

LOGIN_URL = "http://localhost:3000/login"
NOT_FOUND_URL = "http://localhost:3000/not-exist-url"

TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}
ADMIN_CREDENTIALS = {"userId": "A001", "password": "123456"}

@pytest.mark.parametrize("credentials", [
    TEACHER_CREDENTIALS,
    STUDENT_CREDENTIALS,
    ADMIN_CREDENTIALS
])
def test_not_found_page(playwright, credentials):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).to_have_url(re.compile(r"http://localhost:3000/.*"))
    page.goto(NOT_FOUND_URL)
    expect(page).to_have_url(re.compile(r"http://localhost:3000/not-exist-url"))
    # 检查404页面内容
    expect(page.locator("h1:has-text('404')")).to_be_visible()
    expect(page.locator("p:has-text('页面未找到')")).to_be_visible()
    expect(page.locator("p:has-text('抱歉，您访问的页面不存在或已被删除。')")).to_be_visible()
    expect(page.locator("a:has-text('返回首页')")).to_be_visible()
    context.close()
    browser.close()
